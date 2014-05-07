package nl.k3n.util;

import java.io.ByteArrayOutputStream;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLRebuilder {

    private final static Object staticLock = new Object();

    private static XMLOutputFactory factory;

    private static XMLOutputFactory getXMLOutputFactory() {
        synchronized (staticLock) {
            if (factory == null) {
                factory = XMLOutputFactory.newFactory("stax.outputfactory", null);
            }
            return factory;
        }
    }

    private XMLStreamWriter xmlWriter;
    private ByteArrayOutputStream outputStream;

    public XMLRebuilder() {
        outputStream = new ByteArrayOutputStream();
        try {
            xmlWriter = getXMLOutputFactory().createXMLStreamWriter(outputStream);
        } catch (XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void rebuildElement(XMLStreamReader reader) {
        try {
            if (reader.isStartElement()) {
                xmlWriter.writeStartElement(reader.getPrefix(), reader.getLocalName(), reader.getNamespaceURI());
                int count = reader.getAttributeCount();
                for (int i = 0; i < count; i++) {
                    xmlWriter.writeAttribute(reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                }
            } else if (reader.isCharacters()) {
                xmlWriter.writeCharacters(reader.getText());
            } else if (reader.isEndElement()) {
                xmlWriter.writeEndElement();
            }
        } catch (Exception ex) {
            // nothing
        }
    }

    @Override
    public String toString() {
        try {
            xmlWriter.flush();
        } catch (XMLStreamException ex) {
            return "";
        }
        return outputStream.toString();
    }
}
