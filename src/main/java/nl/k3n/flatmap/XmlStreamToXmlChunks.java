
package nl.k3n.flatmap;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamException;
import nl.k3n.aggregators.XMLChunk;
import nl.k3n.aggregators.XMLChunkAggregator;
import nl.k3n.sources.XMLEventSource;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XmlStreamToXmlChunks implements Function<InputStream, Stream<XMLChunk>>{

    @Override
    public Stream<XMLChunk> apply(InputStream t) {
        XMLEventSource eventSource;
        try {
            eventSource = new XMLEventSource(t);
        } catch (IOException | XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
        
        XMLChunkAggregator aggregator = XMLChunkAggregator.BAGAggregator(eventSource.stream());
        return aggregator.stream();
    }
    
}
