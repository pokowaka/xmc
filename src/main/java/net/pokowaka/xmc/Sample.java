package net.pokowaka.xmc;

import net.pokowaka.xmc.interactions.Notification;
import net.pokowaka.xmc.interactions.SimpleCommand;
import net.pokowaka.xmc.io.TransponderDiscovery;
import net.pokowaka.xmc.io.Command;
import net.pokowaka.xmc.io.TransponderNotifications;
import net.pokowaka.xmc.xml.Transponder;

import java.io.IOException;
import java.util.List;

/**
 * Created by erwinj on 1/8/17.
 */
public class Sample {
    public static void main(String args[]) throws IOException, InterruptedException {
        Notification[] notifications = new Notification[]{Notification.video_format, Notification.audio_bits};
        TransponderDiscovery td = new TransponderDiscovery();
        List<Transponder> devices = td.discover();
        for (Transponder et : devices) {
            System.out.println(et);
            TransponderNotifications tn = new TransponderNotifications(et);
            tn.unsubscribe(notifications);
            Command.sendToTransponder(SimpleCommand.standby, et);
        }

        System.exit(0);
    }
}
