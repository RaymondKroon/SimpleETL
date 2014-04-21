
package nl.k3n.transformers;

import java.io.InputStream;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import nl.k3n.interfaces.Source;
import nl.k3n.sources.ZipStreamSource;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipStreamFilter extends FlatMap<InputStream, InputStream> {
    
    private Predicate<ZipEntry> filter = (ZipEntry entry) -> {
        return entry.getName().toLowerCase().endsWith(".zip");
    };
    
    public static Function<InputStream, ZipStreamSource> 
        createMapper (final Predicate<ZipEntry> filter) {
        return (InputStream stream) -> {
            return new ZipStreamSource(stream, filter);
        };
    }
    
    public ZipStreamFilter(Source<InputStream> src, Predicate<ZipEntry> filter) {
        super(src, ZipStreamFilter.createMapper(filter));
    }
}
