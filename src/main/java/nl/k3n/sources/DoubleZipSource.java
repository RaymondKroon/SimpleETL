
package nl.k3n.sources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import nl.k3n.interfaces.Source;
import nl.k3n.transformers.FlatMap;
import static nl.k3n.transformers.ZipMappers.*;
import static nl.k3n.sources.ZipSources.*;

/**
 * I need an 'special' flatmapped double unzipper which stops stream handling.
 * Normal stream.flatMap is lazy, and causes a 'I need to unzip the file completely
 * before I continue' situation
 * 
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class DoubleZipSource implements Source<InputStream> {
    
    public static Predicate<ZipEntry> zipFilter = (ZipEntry entry) -> {
                    return entry.getName().toLowerCase().endsWith(".zip");
                };
    
    private ZipFile zipFile;
    private Stream<InputStream> containerEntries;
    
    private FlatMap<InputStream, InputStream> entryMapper;
    
    public DoubleZipSource(File src, Predicate<ZipEntry> zipEntryFilter) throws IOException {
        this.zipFile = new ZipFile(src);
        
        this.containerEntries = zipFile.stream().filter(e -> !e.isDirectory() && zipFilter.test(e))
                .map(zipEntryToStream(zipFile));
        
        this.entryMapper = new FlatMap<>(containerEntries, 
                (InputStream stream) -> {
                    return zipEntryStream(stream, zipEntryFilter);
                });
    }
    
    @Override
    public Stream<InputStream> stream() {
        return this.entryMapper.stream();
    }

    @Override
    public void close() throws IOException {
        this.zipFile.close();
    }
    
}
