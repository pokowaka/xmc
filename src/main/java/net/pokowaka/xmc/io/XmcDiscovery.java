package net.pokowaka.xmc.io;

import net.pokowaka.xmc.xml.Transponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.*;

/**
 * The XmcDiscovery class is capable of discovering all the XMC-1 devices
 * on the current network. Usually you would use it like this:
 * <p>
 * Xmc xmc = XmcDiscovery.discover();
 */
public class XmcDiscovery {
    public static final int TRANSPONDER_PORT = 7001;
    public static final int DISCOVER_PORT = 7000;

    private static Logger logger = LoggerFactory.getLogger(XmcDiscovery.class);

    private List<TransponderEventListener> listeners = new CopyOnWriteArrayList<TransponderEventListener>();
    private Unmarshaller jaxbUnmarshaller;
    private UdpPacketReceiver receiver;

    public XmcDiscovery() throws IOException {
        this(Executors.newSingleThreadExecutor());
    }

    public XmcDiscovery(ExecutorService es) throws IOException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Transponder.class);
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException ja) {
            logger.error("Failed to instantiate jaxb", ja);
            // Shouldnt 'happen
        }
        receiver = new UdpPacketReceiver(TRANSPONDER_PORT, es);
        receiver.addPacketListener(new TransponderDiscoveryParser());
    }

    public void addTransponderEventListener(TransponderEventListener tel) {
        this.listeners.add(tel);
    }

    public void removeTransponderEventListener(TransponderEventListener tel) {
        this.listeners.remove(tel);
    }

    public void stop() {
        receiver.stop();
    }

    /**
     * Tries to discovers an XMC-1 device on the current network. This is a synchronous call that will block
     * for at most one second. The first XMC-1 device to respond will be returned.
     *
     * @return The first discovered XMC-1 device.
     * @throws IOException
     */
    public static Xmc discover() throws IOException {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Xmc xmc = discover(es);
        try {
            es.shutdown();
            es.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("This shouldn't happen", e);
        }
        return xmc;
    }

    public static Xmc discover(ExecutorService es) throws IOException {
        XmcDiscovery td = new XmcDiscovery(es);
        Transponder xmc = td.discover(1, TimeUnit.SECONDS);
        td.stop();
        return xmc == null ? null : new Xmc(xmc, es);
    }

    /**
     * Tries to discover an XMC-1 device on the current network within the given time frame.
     *
     * @param timeout The maximum amount of time we are willing to wait for a device to respond
     * @param unit    The  unit of time.
     * @return An XMC-1 device, or null if none was found in the given timeframe.
     * @throws IOException
     */
    public Transponder discover(long timeout, TimeUnit unit) throws IOException {
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

        return discovered.isEmpty() ? null : discovered.get(0);
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

    /**
     * The interface for receiving events from the XMC-1
     */
    public interface TransponderEventListener {
        void discovered(Transponder transponder);
    }

    private class TransponderDiscoveryParser implements UdpPacketReceiver.UdpPacketListener {
        @Override
        public void packetReceived(InetSocketAddress from, byte[] packet) {
            ByteArrayInputStream bais = new ByteArrayInputStream(packet);
            try {
                Transponder result = (Transponder) jaxbUnmarshaller.unmarshal(bais);
                result.getControl().setAddress(from);

                logger.info("Discovered " + result);
                for (TransponderEventListener tel : listeners) {
                    tel.discovered(result);
                }
            } catch (Exception e) {
                logger.error("Couldn't unmarshall: " + new String(packet), e);
            }
        }
    }
}
