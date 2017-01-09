package net.pokowaka.xmc.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
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
 */

@XmlRootElement(name = "control")
public class Control {
    @XmlElement(name = "version")
    String version;

    @XmlElement(name = "controlPort")
    int controlPort;

    @XmlElement(name = "notifyPort")
    int notifyPort;

    @XmlElement(name = "infoPort")
    int infoPort;

    @XmlElement(name = "setupPortTCP")
    int setupPortTCP;

    private InetSocketAddress address;

    public SocketAddress getAddress() {
        return address;
    }

    public void setAddress(SocketAddress address) {
        this.address = (InetSocketAddress) address;
    }

    public SocketAddress getControlAddress() {
        return new InetSocketAddress(address.getHostName(), controlPort);
    }


    public SocketAddress getNotifyAddress() {
        return new InetSocketAddress("0.0.0.0", notifyPort);
    }

    public SocketAddress getInfoAddress() {
        return new InetSocketAddress(address.getHostName(), infoPort);
    }


    @Override
    public String toString() {
        return "Control{" +
                "version='" + version + '\'' +
                ", controlPort=" + controlPort +
                ", notifyPort=" + notifyPort +
                ", infoPort=" + infoPort +
                ", setupPortTCP=" + setupPortTCP +
                '}';
    }
}
