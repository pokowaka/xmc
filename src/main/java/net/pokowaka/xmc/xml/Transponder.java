package net.pokowaka.xmc.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.net.InetAddress;
import java.net.SocketAddress;

/**
 * <?xml version="1.0"?>
 * <p/>
 * <emotivaTransponder>
 * <p/>
 * <model>XMC-1</model>
 * <p/>
 * <name>Living Room</name>
 * <p/>
 * <control>
 * <p/>
 * <version>1.0</version>
 * <p/>
 * <controlPort>7002</controlPort>
 * <p/>
 * <notifyPort>7003</notifyPort>
 * <p/>
 * <infoPort>7004</infoPort>
 * <p/>
 * <setupPortTCP>7100</setupPortTCP>
 * <p/>
 * </control>
 * <p/>
 * </emotivaTransponder>
 * Created by erwinj on 1/8/17.
 */

@XmlRootElement(name = "emotivaTransponder")
public class Transponder {
    @XmlElement(name = "model")
    String model;
    @XmlElement(name = "name")
    String name;
    @XmlElement(name = "control")
    Control control;

    public String getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public Control getControl() {
        return control;
    }


    @Override
    public String toString() {
        return "Transponder{" +
                "model='" + model + '\'' +
                ", name='" + name + '\'' +
                ", control=" + control +
                '}';
    }
}
