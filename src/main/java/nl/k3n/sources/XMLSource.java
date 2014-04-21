package nl.k3n.sources;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import nl.k3n.interfaces.Source;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLSource implements Source<XMLEvent> {

    private InputStream srcStream;
    private XMLEventReader reader;
    
    public XMLSource(InputStream srcStream) throws XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        this.srcStream = srcStream;
        this.reader = factory.createXMLEventReader(this.srcStream);
    }
    
    public XMLSource(File src) throws XMLStreamException, FileNotFoundException {        
        this(new FileInputStream(src));
    }

    @Override
    public Iterator<XMLEvent> iterator() {
        return reader;
    }

    @Override
    public void close() throws IOException {
        this.srcStream.close();
    }
}
