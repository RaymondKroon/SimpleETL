
package nl.k3n.transformers;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import nl.k3n.interfaces.Source;
import nl.k3n.spliterator.FixedSizeIteratorSpliterator;
import static nl.k3n.transformers.ZipMappers.*;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipEntryStreamsFromInputStream implements Source<InputStream>, Iterator<InputStream>, Closeable {

    private Predicate<ZipEntry> filter;
    private ZipInputStream zipStream;
    private boolean hasNext = false;
    private ZipEntry currentEntry;
    
    private Stream<InputStream> innerStream;
    
    public ZipEntryStreamsFromInputStream(InputStream src, Predicate<ZipEntry> zipEntryFilter) {
        this.filter = zipEntryFilter;
        this.zipStream = new ZipInputStream(src);
        
        this.innerStream = StreamSupport.stream(new FixedSizeIteratorSpliterator<>(this,
                        Spliterator.IMMUTABLE | Spliterator.NONNULL, 10), true);
        
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
        return zipEntryToStream(this.zipStream).apply(this.currentEntry);
    }
    
    public Stream<InputStream> stream() {
        return this.innerStream;
    }
    
}
