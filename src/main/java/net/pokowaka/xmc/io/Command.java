package net.pokowaka.xmc.io;

import net.pokowaka.xmc.interactions.SimpleCommand;
import net.pokowaka.xmc.interactions.ValueCommand;
import net.pokowaka.xmc.xml.Transponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;


public class Command {
    private static Logger logger = LoggerFactory.getLogger(Command.class);

    private final int value;
    private final ValueCommand valueCommand;
    private final SimpleCommand simpleCommand;
    private boolean ack;

    public Command(SimpleCommand sc) {
        this(sc, false);
    }

    public Command(SimpleCommand sc, boolean ack) {
        this.simpleCommand = sc;
        this.valueCommand = null;
        this.ack = ack;
        this.value = 0;
    }

    public Command(ValueCommand vc, int n) {
        this(vc, n, false);
    }

    public Command(ValueCommand vc, int n, boolean ack) {
        this.simpleCommand = null;
        this.valueCommand = vc;
        this.value = n;
        this.ack = ack;
    }


    public static void sendToTransponder(SimpleCommand sc, Transponder to) throws IOException {
        new Command(sc).sendToTransponder(to);
    }

    public void sendToTransponder(Transponder transponder) throws IOException {
        DatagramChannel send = DatagramChannel.open();
        ByteBuffer bb = ByteBuffer.wrap(this.toXml().getBytes());
        logger.info("sendToTransponder, command: " + toXml() + " to " + transponder.getControl().getControlAddress());
        send.send(bb, transponder.getControl().getControlAddress());
    }

    private String toXml() {
        String command = this.simpleCommand != null ? this.simpleCommand.toString() : this.valueCommand.toString();
        return "<emotivaControl> \n" +
                "  <" + command + " value=\"" + value + "\" ack=\"" + (ack ? "yes" : "no") + "\" /> \n" +
                "</emotivaControl>";
    }
}
