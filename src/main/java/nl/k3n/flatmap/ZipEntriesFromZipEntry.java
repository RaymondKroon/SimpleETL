
package nl.k3n.flatmap;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;
import nl.k3n.sources.ZipStreamSource;
import nl.k3n.zip.SourcedZipEntry;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipEntriesFromZipEntry implements Function<SourcedZipEntry, Stream<SourcedZipEntry>> {

    @Override
    public Stream<SourcedZipEntry> apply(SourcedZipEntry t) {
        
        try {
            return new ZipStreamSource(t.getData(), true).stream();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
