
package nl.k3n.sources;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public interface SourcedZipEntry {
    ZipEntry getEntry();
    InputStream getData() throws IOException;
}
