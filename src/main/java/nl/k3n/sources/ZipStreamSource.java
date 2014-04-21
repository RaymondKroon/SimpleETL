
package nl.k3n.sources;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import nl.k3n.interfaces.Source;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipStreamSource implements Source<InputStream>, Iterator<InputStream> {

    private Predicate<ZipEntry> filter;
    private ZipInputStream zipStream;
    private boolean hasNext = false;
    private ZipEntry currentEntry;
    
    public ZipStreamSource(InputStream src, Predicate<ZipEntry> zipEntryFilter) {
        this.filter = zipEntryFilter;
        this.zipStream = new ZipInputStream(src);
        
    }
    
    private InputStream streamForEntry(ZipEntry entry) throws IOException {
        long size = entry.getSize();
        int intSize = size > 0 && size < Integer.MAX_VALUE ? (int) size : 0;
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(intSize);
        ByteStreams.copy(this.zipStream, buffer);
        
        return new ByteArrayInputStream(buffer.toByteArray());
    }
    
    @Override
    public Iterator<InputStream> iterator() {
        return this;
    }

    @Override
    public void close() throws IOException {
        this.zipStream.close();
    }

    @Override
    public boolean hasNext() {
        if (!this.hasNext) {
            try {
                ZipEntry nextEntry = null;
                while (true) 
                {
                    nextEntry = this.zipStream.getNextEntry();
                    if (nextEntry == null) {
                        break;
                    }
                    
                    if (nextEntry != null && this.filter.test(nextEntry)) {
                        this.hasNext = true;
                        this.currentEntry = nextEntry;
                        break;
                    }
                }
            } catch (IOException ex) {
                return false;
            }
        }
        
        return this.hasNext;
    }

    @Override
    public InputStream next() {
        if (!this.hasNext) {
            return null;
        }
        
        this.hasNext = false;
        try {
            return this.streamForEntry(this.currentEntry);
        } catch (IOException ex) {
            throw new RuntimeException("could not create stream", ex);
        }
    }
    
}
