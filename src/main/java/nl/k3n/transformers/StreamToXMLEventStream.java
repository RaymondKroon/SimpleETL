
package nl.k3n.transformers;

import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import nl.k3n.interfaces.Source;
import nl.k3n.sources.XMLSource;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class StreamToXMLEventStream extends FlatMap<InputStream, XMLEvent> {
    
    public static Source<XMLEvent> mapper (InputStream stream) {
        try {
            return new XMLSource(stream);
        } catch (XMLStreamException ex) {
            throw new RuntimeException("Could not create XMLSource", ex);
        }
    }
    
    public StreamToXMLEventStream(Source<InputStream> src) {
        super(src, StreamToXMLEventStream::mapper);
    }
}
