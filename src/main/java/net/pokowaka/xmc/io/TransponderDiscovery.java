package net.pokowaka.xmc.io;

import net.pokowaka.xmc.xml.Transponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by erwinj on 1/8/17.
 */
public class TransponderDiscovery {
    public static final int TRANSPONDER_PORT = 7001;
    public static final int DISCOVER_PORT = 7000;

    private static Logger logger = LoggerFactory.getLogger(TransponderDiscovery.class);

    private List<TransponderEventListener> listeners = new CopyOnWriteArrayList<TransponderEventListener>();
    private Unmarshaller jaxbUnmarshaller;

    DatagramChannel channel;
    boolean mActive = true;

    public TransponderDiscovery() throws IOException {
        this(Executors.newSingleThreadExecutor());
    }

    public TransponderDiscovery(ExecutorService es) throws IOException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Transponder.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException ja) {
            logger.error("Failed to instantiate jaxb", ja);
            // Shouldnt 'happen
        }

        channel = DatagramChannel.open();
        channel.bind(new InetSocketAddress(TRANSPONDER_PORT));

        Runnable task = () -> {
            this.listen();
        };
        es.submit(task);
    }

    public void addTransponderEventListener(TransponderEventListener tel) {
        this.listeners.add(tel);
    }


    public void removeTransponderEventListener(TransponderEventListener tel) {
        this.listeners.remove(tel);
    }

    public List<Transponder> discover() throws IOException {
        return discover(1, TimeUnit.SECONDS);
    }

    public List<Transponder> discover(long timeout, TimeUnit unit) throws IOException {
        final ArrayList<Transponder> discovered = new ArrayList<Transponder>();
        final Semaphore lock = new Semaphore(1);
        try {
            lock.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.addTransponderEventListener(new TransponderEventListener() {
            @Override
            public void discovered(Transponder transponder) {
                discovered.add(transponder);
                lock.release();
            }
        });

        discoverAysnc();

        try {
            lock.tryAcquire(timeout, unit);
        } catch (InterruptedException e) {

        }

        return discovered;
    }

    public void discoverAysnc() throws IOException {
        ByteBuffer ping = ByteBuffer.wrap("<emotivaPing/>".getBytes());

        DatagramChannel send = DatagramChannel.open();
        send.socket().setBroadcast(true);
        for (InetAddress address : findBroadcastAddresses()) {
            InetSocketAddress addr = new InetSocketAddress(address.getHostAddress(), DISCOVER_PORT);
            logger.debug("Broadcast to " + addr);
            send.send(ping, addr);
        }
    }

    public void listen() {
        ByteBuffer buf = ByteBuffer.allocate(2048);
        while (mActive) {
            buf.clear();

            try {

                SocketAddress remote = channel.receive(buf);
                ByteBufferInputStream bais = new ByteBufferInputStream(buf);
                Transponder result = (Transponder) jaxbUnmarshaller.unmarshal(bais);
                result.getControl().setAddress(remote);

                logger.info("Discovered " + result);
                for (TransponderEventListener tel : listeners) {
                    tel.discovered(result);
                }
            } catch (Exception e) {
                logger.error("Failed to deserialize object: " + new String(buf.array()), e);
            }
        }
    }

    private ArrayList<InetAddress> findBroadcastAddresses() throws SocketException {
        ArrayList<InetAddress> list = new ArrayList<InetAddress>();
        Enumeration<NetworkInterface> interfaces =
                NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (networkInterface.isLoopback())
                continue;    // Don't want to broadcast to the loopback interface
            for (InterfaceAddress interfaceAddress :
                    networkInterface.getInterfaceAddresses()) {
                InetAddress broadcast = interfaceAddress.getBroadcast();
                if (broadcast == null)
                    continue;
                list.add(broadcast);
            }
        }

        return list;
    }
}
