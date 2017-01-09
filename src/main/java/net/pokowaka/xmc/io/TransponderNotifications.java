package net.pokowaka.xmc.io;

import net.pokowaka.xmc.interactions.Notification;
import net.pokowaka.xmc.xml.Transponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by erwinj on 1/8/17.
 */
public class TransponderNotifications {

    private static Logger logger = LoggerFactory.getLogger(TransponderDiscovery.class);

    private List<TransponderEventListener> listeners = new CopyOnWriteArrayList<TransponderEventListener>();
    private Unmarshaller jaxbUnmarshaller;

    DatagramChannel channel;
    boolean mActive = true;
    Transponder transponder;

    public TransponderNotifications(Transponder transponder) throws IOException {
        this(transponder, Executors.newSingleThreadExecutor());
    }

    public TransponderNotifications(Transponder transponder, ExecutorService es) throws IOException {
        this.transponder = transponder;
        channel = DatagramChannel.open();
        channel.bind(this.transponder.getControl().getNotifyAddress());

        Runnable task = () -> {
            this.listen();
        };
        es.submit(task);
    }

    public void subscribe(Notification[] notifications) throws IOException {
        DatagramChannel send = DatagramChannel.open();
        String xml = this.subscribeXml(notifications);
        ByteBuffer bb = ByteBuffer.wrap(xml.getBytes());
        logger.info("sendToTransponder, command: " + xml + " to " + transponder.getControl().getControlAddress());
        send.send(bb, transponder.getControl().getControlAddress());
    }


    public void unsubscribe(Notification[] notifications) throws IOException {
        DatagramChannel send = DatagramChannel.open();
        String xml = this.unsubscribeXml(notifications);
        ByteBuffer bb = ByteBuffer.wrap(xml.getBytes());
        logger.info("sendToTransponder, command: " + xml + " to " + transponder.getControl().getControlAddress());
        send.send(bb, transponder.getControl().getControlAddress());
    }


    private String unsubscribeXml(Notification[] notifications) {
        String xml = "<emotivaUnsubscribe>";
        for (Notification n : notifications) {
            xml = xml + "<" + n.toString() + " />";
        }
        xml += "</emotivaUnsubscribe>";
        return xml;
    }


    private String subscribeXml(Notification[] notifications) {
        String xml = "<emotivaSubscription>";
        for (Notification n : notifications) {
            xml = xml + "<" + n.toString() + " />";
        }
        xml += "</emotivaSubscription>";
        return xml;
    }

    public void listen() {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        while (mActive) {
            buf.clear();

            try {

                SocketAddress remote = channel.receive(buf);
                ByteBufferInputStream bbis = new ByteBufferInputStream(buf);
                try (BufferedReader buffer = new BufferedReader(new InputStreamReader(bbis))) {
                    String lines = buffer.lines().collect(Collectors.joining("\n"));
                    logger.info("Received " + lines);
                }
            } catch (Exception e) {
                logger.error("Failed to deserialize object: " + new String(buf.array()), e);
            }
        }

    }
}
