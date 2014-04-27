
package nl.k3n.sources;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import nl.k3n.interfaces.Source;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class ZipFileSource implements Source<SourcedZipEntry> {

    ZipFile zipFile;
    Stream<SourcedZipEntry> innerStream;
    
    public ZipFileSource(File file) throws IOException {
        this.zipFile = new ZipFile(file);
        
        this.innerStream = this.createStream();
    }
    
    private Stream<SourcedZipEntry> createStream() {
        return this.zipFile.stream().map((ZipEntry e) -> new ZipFileEntry(zipFile, e));
    }
    
    @Override
    public Stream<SourcedZipEntry> stream() {
        return this.innerStream;
    }

    @Override
    public void close() throws IOException {
        this.zipFile.close();
    }
    
}
