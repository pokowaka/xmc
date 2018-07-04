package net.pokowaka.xmc.io;

import net.pokowaka.xmc.interactions.Command;
import net.pokowaka.xmc.interactions.XmcState;
import net.pokowaka.xmc.interactions.SimpleCommand;
import net.pokowaka.xmc.interactions.ValueCommand;
import net.pokowaka.xmc.xml.NotificationParser;
import net.pokowaka.xmc.xml.Transponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Represents the XMC-1. Usually you will get a reference to this by using the @see XmcDiscovery
 * class. <p> NOTE: This currently is a singleton as we can really only handle communication with
 * one Xmc-1 on the network.
 */
public class Xmc {
    private static Logger logger = LoggerFactory.getLogger(Xmc.class);
    private final UdpPacketReceiver notifications;
    private final UdpPacketReceiver control;
    private final ExecutorService es;
    private final Transponder transponder;
    private final HashMap<XmcState, String> status = new HashMap<>();
    private final List<Xmc1StateListener> listeners = new CopyOnWriteArrayList<>();

    private boolean terminate = false;

    Xmc(Transponder transponder) throws IOException {
        this(transponder, Executors.newSingleThreadExecutor());
        terminate = true;
    }

    Xmc(Transponder transponder, ExecutorService es) throws IOException {
        this.transponder = transponder;
        this.es = es;
        this.notifications =
            new UdpPacketReceiver(transponder.getControl().getNotifyAddress().getPort(), es);
        this.control =
            new UdpPacketReceiver(transponder.getControl().getControlAddress().getPort());
        this.notifications.addPacketListener(new UdpPacketReceiver.UdpPacketListener() {
            @Override
            public void packetReceived(InetSocketAddress from, byte[] packet) {
                HashMap<XmcState, String> updated = NotificationParser.parse(new String(packet));
                HashMap<XmcState, String> old = new HashMap<>();
                for (XmcState n : updated.keySet()) {
                    old.put(n, status.getOrDefault(n, ""));
                    status.put(n, updated.get(n));
                }

                for (Xmc1StateListener listener : listeners) {
                    listener.stateChange(Xmc.this, old, updated);
                }
            }
        });
    }

    public String getValue(XmcState n) {
        return status.get(n);
    }

    public void addXmc1StateListener(Xmc1StateListener l) {
        this.listeners.add(l);
    }

    public void removeXmc1StateListener(Xmc1StateListener l) {
        this.listeners.remove(l);
    }

    /**
     * Stops any interaction with the Xmc-1, releasing the port and stopping
     * any threads, shutting down the executor services if needed.
     */
    public void stop() {
        this.notifications.stop();
        this.control.stop();
        if (terminate) {
            try {
                es.shutdown();
                es.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Shouldn't happen", e);
            }
        }
    }

    public void subscribe(Iterable<XmcState> notifications) throws IOException {
        String xml = this.subscribeXml(notifications);
        String reply = send(xml);
        logger.info("subscribe: " + reply);

        HashMap<XmcState, String> map = NotificationParser.parse(reply);
        for (XmcState n : map.keySet()) {
            status.put(n, map.get(n));
        }
    }

    /**
     * Subscrive to the given array of notifications.
     *
     * @param notifications
     * @throws IOException
     */
    public void subscribe(XmcState[] notifications) throws IOException {
        subscribe(Arrays.asList(notifications));
    }

    /**
     * Unsubscribe from the given set of notifications.
     *
     * @param notifications Array of notifications from which to unsubscribe.
     * @throws IOException
     */
    public void unsubscribe(XmcState[] notifications) throws IOException {
        unsubscribe(Arrays.asList(notifications));
    }

    public void unsubscribe(Iterable<XmcState> notifications) throws IOException {
        String reply = send(this.unsubscribeXml(notifications));
        logger.info("Unsubscribe: " + reply);
    }

    /**
     * Sends the given xml string and waits for a reply from the XMC-1.
     *
     * @param xml
     * @return
     * @throws IOException
     */
    public String send(String xml) throws IOException {
        logger.info("send: xml: " + xml + " to " + transponder.getControl().getControlAddress());

        byte[] send = xml.getBytes();
        InetSocketAddress address = transponder.getControl().getControlAddress();
        byte[] received = control.sendAndWait(1, TimeUnit.SECONDS, send, address);
        return new String(received);
    }

    /**
     * Sends an emotiva command, returning the reply of the Xmc-1 if an ack is requested.
     * <p>
     * If the Xmc does not reply in a timely fashion, this method will return an empty string.
     *
     * @param cmd The command to execute.
     * @return The ack string, or nothing.
     * @throws IOException
     */
    public String send(Command cmd) throws IOException {
        if (cmd.getAck()) {
            return send(cmd.toXml());
        }

        UdpPacketReceiver.send(
            cmd.toXml().getBytes(), transponder.getControl().getControlAddress());
        return "";
    }

    /**
     * Sends a simple command (a command without any parameters) to the given XMC-1
     *
     * @param sc The command to send.
     * @throws IOException
     */
    public void send(SimpleCommand sc) throws IOException {
        send(new Command(sc));
    }

    /**
     * Sends a command that takes a parameter to the given XMC-1
     *
     * @param sc    The command to send.
     * @param value The value associated with this command
     * @throws IOException
     */
    public void send(ValueCommand sc, int value) throws IOException {
        send(new Command(sc, value));
    }

    private String unsubscribeXml(Iterable<XmcState> notifications) {
        String xml = "<emotivaUnsubscribe>";
        for (XmcState n : notifications) {
            xml = xml + "<" + n.toString() + " />";
        }
        xml += "</emotivaUnsubscribe>";
        return xml;
    }

    private String subscribeXml(Iterable<XmcState> notifications) {
        String xml = "<emotivaSubscription>";
        for (XmcState n : notifications) {
            xml = xml + "<" + n.toString() + " />";
        }
        xml += "</emotivaSubscription>";
        return xml;
    }

    @Override
    public String toString() {
        return "Xmc{"
            + "status=" + status + '}';
    }

    public interface Xmc1StateListener {
        void stateChange(Xmc xmc, HashMap<XmcState, String> old, HashMap<XmcState, String> updated);
    }
}
