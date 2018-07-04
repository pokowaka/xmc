package net.pokowaka.xmc;

import net.pokowaka.xmc.interactions.XmcState;
import net.pokowaka.xmc.interactions.ValueCommand;
import net.pokowaka.xmc.io.Xmc;
import net.pokowaka.xmc.io.XmcDiscovery;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by erwinj on 1/8/17.
 */
public class Sample {
    public static void main(String args[]) throws IOException, InterruptedException {
        XmcState[] all = XmcState.values();
        Xmc xmc  = XmcDiscovery.discover();

        if (xmc == null) {
            System.out.println("Cannot find Xmc-1");
            return;
        }
        // Register for events..
        xmc.addXmc1StateListener(new Xmc.Xmc1StateListener() {
            @Override
            public void stateChange(Xmc xmc, HashMap<XmcState, String> old, HashMap<XmcState, String> updated) {
                System.out.println("Old: " + old);
                System.out.println("New: " + updated);
            }
        });

        xmc.subscribe(all);
        xmc.send(ValueCommand.volume, -1);

        // Print the status
        System.out.println(xmc);

        // Sleep a bit
        Thread.sleep(TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS));

        // Make sure the Xmc-1 stops sending out data
        xmc.unsubscribe(all);

        // And stop all the threads..
        xmc.stop();
    }
}
