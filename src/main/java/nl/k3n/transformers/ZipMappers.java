
package nl.k3n.transformers;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import nl.k3n.util.UncloseableBufferedInputStream;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipMappers {
    public static Function<ZipEntry, InputStream> zipEntryToStream(final ZipFile zip) {
        return (ZipEntry entry) -> {
            try {
                return zip.getInputStream(entry);   
            } catch (IOException ex) {
                throw new RuntimeException("Cannot creat entry stream", ex);
            }
        };
    }
    
    public static Function<ZipEntry, InputStream> zipEntryToStream(final ZipInputStream zipStream) {
        return (ZipEntry entry) -> {
            System.out.println(entry.getName());
            return new UncloseableBufferedInputStream(zipStream);
        };
    }
}
