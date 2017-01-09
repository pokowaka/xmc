package net.pokowaka.xmc.io;

import net.pokowaka.xmc.xml.Transponder;

/**
 * Created by erwinj on 1/8/17.
 */
public interface TransponderEventListener {
    void discovered(Transponder transponder);
}
