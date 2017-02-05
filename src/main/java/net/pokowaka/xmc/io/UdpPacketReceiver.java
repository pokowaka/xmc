package net.pokowaka.xmc.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.concurrent.*;

/**
 * A class that is capable of receiving UDP packets on a given port.
 */
public class UdpPacketReceiver {

    public static final String LOCALHOST = "127.0.0.1";
    private static Logger logger = LoggerFactory.getLogger(UdpPacketReceiver.class);
    private final ExecutorService es;

    private final List<UdpPacketListener> listeners = new CopyOnWriteArrayList<>();
    private final int port;
    private final DatagramSocket datagram;
    boolean mActive = true;
    boolean terminate = false;

    public UdpPacketReceiver(int port) throws IOException {
        this(port, Executors.newSingleThreadExecutor());
        terminate = true;
    }

    public UdpPacketReceiver(int port, ExecutorService es) throws IOException {
        this.es = es;
        this.port = port;
        this.datagram = new DatagramSocket(port);
        es.submit(() -> {
            listen();
        });
    }

    public void addPacketListener(UdpPacketListener listen) {
        this.listeners.add(listen);
    }

    public void removePacketListener(LockListener lockListener) {
        this.listeners.remove(lockListener);
    }

    /**
     * Stops the packet receiver from listening to the port and stops the thread.
     */
    public void stop() {
        logger.info("Stopping " + this);
        mActive = false;
        try {
            ByteBuffer emptyBuffer = ByteBuffer.allocate(0);
            DatagramChannel send = DatagramChannel.open();
            send.send(emptyBuffer, new InetSocketAddress(LOCALHOST, port));
        } catch (IOException e) {
            logger.error("Unable to disconnect the channel", e);
        }
        if (terminate) {
            this.es.shutdown();
            try {
                this.es.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Shouldn't happen", e);
            }
        }
    }

    @Override
    public String toString() {
        return "UdpPacketReceiver{" +
                "port=" + port +
                ", mActive=" + mActive +
                '}';
    }

    public void listen() {
        logger.info("Listening for packets " + this);
        byte buf[] = new byte[4096];
        while (mActive) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                datagram.receive(packet);
                if (packet.getLength() == 0) {
                    continue;
                }

                byte received[] = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, received, 0, packet.getLength());

                if (logger.isDebugEnabled()) logger.debug("Received: " + new String(received));

                for (UdpPacketListener l : listeners) {
                    l.packetReceived(new InetSocketAddress(packet.getAddress(), port), received);
                }
            } catch (IOException e) {
                logger.error("Failed to receive datagram packet", e);
            }
        }
        logger.info("Finished " + this);
    }

    public static void send(byte[] toSend, InetSocketAddress address) throws IOException {
        logger.debug("Sending " + new String(toSend) + " to " + address);
        DatagramChannel send = DatagramChannel.open();
        ByteBuffer bb = ByteBuffer.wrap(toSend);
        send.send(bb, address);
    }

    byte[] sendAndWait(long timeout, TimeUnit unit, byte[] toSend, InetSocketAddress address) throws IOException {
        try {
            LockListener listener = new LockListener(this);
            send(toSend, address);
            return listener.getPacket(timeout, unit);
        } catch (InterruptedException ie) {
            logger.info("Interrupted..", ie);
            return new byte[0];
        }
    }

    private class LockListener implements UdpPacketListener {
        private final Semaphore lock;
        private final UdpPacketReceiver receiver;
        private byte[] packet;

        public LockListener(UdpPacketReceiver receiver) throws InterruptedException {
            this.lock = new Semaphore(1);
            this.lock.acquire();
            this.receiver = receiver;
            this.receiver.addPacketListener(this);
        }

        public void packetReceived(InetSocketAddress from, byte[] packet) {
            this.packet = packet;
            this.receiver.removePacketListener(this);
            this.lock.release();
        }

        public byte[] getPacket(long timeout, TimeUnit unit) throws InterruptedException {
            lock.tryAcquire(timeout, unit);
            return this.packet;
        }
    }

    /**
     * Created by erwinj on 2/4/17.
     */
    public interface UdpPacketListener {

        void packetReceived(InetSocketAddress from, byte[] packet);
    }


}
