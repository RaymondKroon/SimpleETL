
package nl.k3n.flatmap;

import java.io.InputStream;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.xml.stream.XMLStreamException;
import nl.k3n.aggregators.XMLStreamAggregator;
import nl.k3n.entity.builder.GeometryBuilder;
import org.deegree.feature.types.AppSchema;
import org.deegree.geometry.Geometry;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLStreamToGeometries implements Function<InputStream, Stream<Geometry>> {

    private final AppSchema appSchema;
    
    public XMLStreamToGeometries(AppSchema appSchema) {
        this.appSchema = appSchema;
    }
    
    @Override
    public Stream<Geometry> apply(InputStream t) {
        try {
            XMLStreamAggregator<Geometry> aggr =
                    new XMLStreamAggregator<>(t, new GeometryBuilder(appSchema));
            
            return aggr.stream();
            
        } catch (XMLStreamException ex) {
            return Stream.empty();
        }
    }
    
}
