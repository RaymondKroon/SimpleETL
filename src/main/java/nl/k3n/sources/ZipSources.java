
package nl.k3n.sources;

import nl.k3n.transformers.ZipEntryStreamsFromInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import static nl.k3n.transformers.ZipMappers.*;
import static nl.k3n.util.Wrappers.*;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipSources {
    
    public static Stream<InputStream> zipEntryStream(File src, Predicate<ZipEntry> zipEntryFilter) throws FileNotFoundException, IOException {
        ZipFile zipFile = new ZipFile(src);
        return zipFile.stream().filter(e -> !e.isDirectory() && zipEntryFilter.test(e))
                .map(zipEntryToStream(zipFile))
                .onClose(uncheckedAction(zipFile::close));
    }
    
    public static Stream<InputStream> zipEntryStream(InputStream src, Predicate<ZipEntry> zipEntryFilter) {
        ZipEntryStreamsFromInputStream streamSrc = new ZipEntryStreamsFromInputStream(src, zipEntryFilter);
        return streamSrc.stream();
        
    }
}
