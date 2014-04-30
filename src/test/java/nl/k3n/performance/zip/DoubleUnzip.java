
package nl.k3n.performance.zip;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import nl.k3n.sources.SourcedZipEntry;
import nl.k3n.sources.ZipFileSource;
import nl.k3n.sources.ZipStreamSource;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Use a container zip with zips with xml files
 * @author Raymond Kroon <raymond@k3n.nl>
 */
@Ignore
public class DoubleUnzip {
    
    public static String FILENAME = "D:\\Downloads\\inspireadressen.zip";
    
    public DoubleUnzip() {
    }

    @Test
    public void singleThreaded() throws IOException {
        try (ZipFileSource zipFileSource = new ZipFileSource(new File(FILENAME))) {
            zipFileSource.stream()
                    .filter(e -> !e.getEntry().isDirectory())
                    .flatMap(this::streamToEntry)
                    .filter(e -> !e.getEntry().isDirectory())
                    .forEach(this::zipConsumer);
        }
    }
    
    @Test
    public void multiThreaded() throws IOException {
        try (ZipFileSource zipFileSource = new ZipFileSource(new File(FILENAME))) {
            zipFileSource.stream().parallel()
                    .filter(e -> !e.getEntry().isDirectory())
                    .flatMap(this::streamToEntry)
                    .filter(e -> !e.getEntry().isDirectory())
                    .forEach(this::zipConsumer);
        }
    }
    
    private void zipConsumer(SourcedZipEntry entry) {
        try {
            LocalTime start = LocalTime.now();
            
            ByteArrayOutputStream target = new ByteArrayOutputStream();
            ByteStreams.copy(entry.getData(), target);
            
            assertTrue(target.size() > 0);
            LocalTime stop = LocalTime.now();
            System.out.format("%s: %s%n - %s%n", entry.getEntry().getName(), start, stop);

            // and forget about it :)
        } catch (IOException ex) {
            Logger.getLogger(ZipContainerUnzip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private Stream<SourcedZipEntry> streamToEntry(SourcedZipEntry zipSource) {
        ZipStreamSource zipStreamSource;
        try {
            zipStreamSource = new ZipStreamSource(zipSource.getData(), false);
            return zipStreamSource.stream();
        } catch (IOException ex) {
            return Stream.empty();
        }
    }
}
