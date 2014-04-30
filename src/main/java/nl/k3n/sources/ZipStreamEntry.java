
package nl.k3n.sources;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import nl.k3n.util.UncloseableBufferedInputStream;

/**
 * Do not use this class by itself. It only works within a stream.
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipStreamEntry implements SourcedZipEntry {

    private ZipInputStream zipStream;
    private ZipEntry entry;
    boolean buffered = false;
    private byte[] data;
    
    public ZipStreamEntry(ZipInputStream zipInputStream, ZipEntry entry) {
        this.zipStream = zipInputStream;
        this.entry = entry;
    }
    
    public ZipStreamEntry(ZipInputStream zipInputStream, ZipEntry entry, boolean buffered) throws IOException {
        this(zipInputStream, entry);
        
        this.buffered = buffered;
        if (buffered) {
            this.data = ByteStreams.toByteArray(zipStream);
        }
    }
    
    @Override
    public ZipEntry getEntry() {
        return this.entry;
    }

    @Override
    public InputStream getData() throws IOException {
        return this.buffered ?
                  new ByteArrayInputStream(this.data) 
                : new UncloseableBufferedInputStream(this.zipStream);
    }
    
}
