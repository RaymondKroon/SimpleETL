
package nl.k3n.sources;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipFileEntry implements SourcedZipEntry {

    private ZipFile zipFile;
    private ZipEntry entry;
    
    public ZipFileEntry(ZipFile zipFile, ZipEntry entry) {
        this.zipFile = zipFile;
        this.entry = entry;
    }
    
    @Override
    public ZipEntry getEntry() {
        return this.entry;
    }

    @Override
    public InputStream getData() throws IOException {
        return this.zipFile.getInputStream(this.entry);
    }
    
}
