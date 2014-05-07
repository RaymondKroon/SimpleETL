
package nl.k3n.flatmap;

import java.io.InputStream;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamException;
import nl.k3n.aggregators.XMLStreamAggregator;
import nl.k3n.entity.GMLChunk;
import nl.k3n.entity.builder.GMLChunkBuilder;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLStreamToGMLChunks implements Function<InputStream, Stream<GMLChunk>> {

    @Override
    public Stream<GMLChunk> apply(InputStream t) {
        try {
            XMLStreamAggregator<GMLChunk> aggr =
                    new XMLStreamAggregator<>(t, new GMLChunkBuilder());
            
            return aggr.stream();
            
        } catch (XMLStreamException ex) {
            return Stream.empty();
        }
    }
    
}
