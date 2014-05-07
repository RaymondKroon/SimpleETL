
package nl.k3n.sources;

import nl.k3n.zip.impl.ZipStreamEntry;
import nl.k3n.zip.SourcedZipEntry;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import nl.k3n.interfaces.Source;
import nl.k3n.spliterator.FixedSizeIteratorSpliterator;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipStreamSource implements Source<SourcedZipEntry> {

    private boolean buffered = false;
    
    ZipInputStream zipStream;
    Stream<SourcedZipEntry> innerStream;
    
    public ZipStreamSource(InputStream inputStream, boolean buffered) {
        this(new ZipInputStream(inputStream), buffered);
    }
    
    public ZipStreamSource(ZipInputStream zipStream, boolean buffered) {
        this.zipStream = zipStream;
        this.innerStream = this.createStream();
        
        this.buffered = buffered;
    }
    
    private Stream<SourcedZipEntry> createStream() {
        return StreamSupport.stream(new FixedSizeIteratorSpliterator<>(
                new ZipEntryIterator(), Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL,
        1), false);
    }
    
    @Override
    public Stream<SourcedZipEntry> stream() {
        return this.innerStream;
    }

    private SourcedZipEntry convert(ZipEntry entry) {
        try {
            return new ZipStreamEntry(ZipStreamSource.this.zipStream, entry, buffered);
        } catch (IOException ex) {
            return new ZipStreamEntry(ZipStreamSource.this.zipStream, entry);
        }
    }
    
    @Override
    public void close() throws IOException {
        //
    }

    private class ZipEntryIterator implements Enumeration<SourcedZipEntry>, Iterator<SourcedZipEntry> {
        private int i = 0;
        
        private boolean hasNext = false;
        private ZipEntry currentEntry;
        
        public ZipEntryIterator() {
        }

        @Override
        public boolean hasMoreElements() {
            return hasNext();
        }

        @Override
        public boolean hasNext() {
            synchronized(ZipStreamSource.this) {
                if (!this.hasNext) {
                    try {
                        ZipEntry nextEntry = null;
                        while (true) 
                        {
                            nextEntry = ZipStreamSource.this.zipStream.getNextEntry();
                            if (nextEntry == null) {
                                break;
                            }

                            if (nextEntry != null) {
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
        }

        public SourcedZipEntry nextElement() {
            return next();
        }

        @Override
        public SourcedZipEntry next() {
            synchronized(ZipStreamSource.this) {
                if (!this.hasNext) {
                    return null;
                }

                this.hasNext = false;
                return ZipStreamSource.this.convert(currentEntry);
            }
        }
    }
    
}
