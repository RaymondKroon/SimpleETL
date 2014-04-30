package nl.k3n.performance.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
@Ignore
public class CountElementsTest {

    public static String FILENAME = "D:\\Downloads\\inspireadressen\\9999PND08032014\\9999PND08032014-000001.xml";

    public CountElementsTest() {
    }
    
    @Before
    public void setup() {
        System.setProperty("stax.packedImplementation", "com.sun.xml.internal.stream.XMLInputFactoryImpl");
        System.setProperty("stax.aalto", "com.fasterxml.aalto.stax.InputFactoryImpl");
    }
    
    @Test
    public void runPacked() throws FileNotFoundException, XMLStreamException, IOException {
        
        XMLInputFactory factory = XMLInputFactory.newFactory("stax.packedImplementation", null);
        for (int i = 0; i < 50; i++) {
            try (InputStream fileStream = new FileInputStream(new File(FILENAME))) {
                XMLStreamReader reader = factory.createXMLStreamReader(fileStream);

                System.out.println(countStartElements(reader));
            }
        }
    }
    
    @Test
    public void runAalto() throws FileNotFoundException, XMLStreamException, IOException {
        
        XMLInputFactory factory = XMLInputFactory.newFactory();
        for (int i = 0; i < 50; i++) {
            try (InputStream fileStream = new FileInputStream(new File(FILENAME))) {
                XMLStreamReader reader = factory.createXMLStreamReader(fileStream);

                System.out.println(countStartElements(reader));
            }
        }
    }

    private int countStartElements(XMLStreamReader reader) throws XMLStreamException {
        int count = 0;
        while (reader.hasNext()) {
            reader.next();
            if (reader.isStartElement() && reader.getLocalName().equals("Pand")) {
                count += 1;
            }
        }

        return count;
    }
}
