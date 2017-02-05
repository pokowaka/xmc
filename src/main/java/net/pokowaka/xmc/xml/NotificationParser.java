package net.pokowaka.xmc.xml;

import net.pokowaka.xmc.interactions.XmcState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by erwinj on 2/4/17.
 */
public class NotificationParser {

    private static Logger logger = LoggerFactory.getLogger(NotificationParser.class);

    public static HashMap<XmcState, String> parse(String xml) {
        HashMap<XmcState, String> status = new HashMap<>();
        try {
            doParse(xml, status);
        } catch (Exception e) {
            logger.error("Failed to parse response: " + xml, e);
        }
        return status;
    }

    private static void doParse(String xml, HashMap<XmcState, String> status) throws ParserConfigurationException, SAXException, IOException {
        Document doc = getDocument(xml);
        Element root = doc.getDocumentElement();

        NodeList nList = root.getChildNodes();
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() != Node.ELEMENT_NODE)
                continue;

            try {
                XmcState n = XmcState.valueOf(nNode.getNodeName());
                status.put(n, nNode.getAttributes().getNamedItem("value").getNodeValue());
            } catch (IllegalArgumentException ia) {
                logger.error("Unknown constant, for: " + nNode.getNodeName());
            }
        }
    }

    private static Document getDocument(String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new ByteArrayInputStream(xml.getBytes()));

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();
        return doc;
    }
}
