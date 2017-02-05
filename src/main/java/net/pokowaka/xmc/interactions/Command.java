package net.pokowaka.xmc.interactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Command that can be send to the XMC-1
 */
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

    public String toXml() {
        String command = this.simpleCommand != null ? this.simpleCommand.toString() : this.valueCommand.toString();
        return "<emotivaControl> \n" +
                "  <" + command + " value=\"" + value + "\" ack=\"" + (ack ? "yes" : "no") + "\" /> \n" +
                "</emotivaControl>";
    }

    public boolean getAck() {
        return ack;
    }
}
