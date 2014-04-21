
package nl.k3n.sources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import nl.k3n.interfaces.Source;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipFileSource implements Source<InputStream>{

    private Predicate<ZipEntry> zipEntryFilter;
    private ZipFile zipFile;
    private Iterator<InputStream> iterator;
    
    public ZipFileSource(File src, Predicate<ZipEntry> zipEntryFilter) throws FileNotFoundException, IOException {
        this.zipEntryFilter = zipEntryFilter;
        this.zipFile = new ZipFile(src);
        this.iterator = this.zipFile.stream().filter(e -> !e.isDirectory() && zipEntryFilter.test(e))
                .map(this::streamForEntry).iterator();
        
    }
    
    private InputStream streamForEntry(ZipEntry entry) {
        long size = entry.getSize();
        try {
            return this.zipFile.getInputStream(entry);
        } catch (IOException ex) {
            throw new RuntimeException("Cannot creat entry stream", ex);
        }
    }
    
    @Override
    public Iterator<InputStream> iterator() {
        return iterator;
    }

    @Override
    public void close() throws IOException {
        this.zipFile.close();
    }
    
}
