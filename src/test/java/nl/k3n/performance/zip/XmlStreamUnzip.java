
package nl.k3n.performance.zip;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import nl.k3n.zip.SourcedZipEntry;
import nl.k3n.sources.ZipStreamSource;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Use a zip with xml files
 * @author Raymond Kroon <raymond@k3n.nl>
 */
@Ignore
public class XmlStreamUnzip {
    
    public static String FILENAME = "D:\\Downloads\\inspireadressen\\9999PND08032014.zip";
    
    public XmlStreamUnzip() {
    }

    @Test
    public void singleThreaded() throws IOException {
        try (FileInputStream stream = new FileInputStream(new File(FILENAME))) {
            ZipStreamSource zipFileSource = new ZipStreamSource(stream, false);
            zipFileSource.stream()
                    .filter(e -> !e.getEntry().isDirectory())
                    .forEach(this::zipConsumer);
        }
    }
    
    @Test
    public void bufferedSingleThreaded() throws IOException {
        try (FileInputStream stream = new FileInputStream(new File(FILENAME))) {
            ZipStreamSource zipFileSource = new ZipStreamSource(stream, true);
            zipFileSource.stream()
                    .filter(e -> !e.getEntry().isDirectory())
                    .forEach(this::zipConsumer);
        }
    }
    
    @Test
    public void multiThreaded() throws IOException {
        try (FileInputStream stream = new FileInputStream(new File(FILENAME))) {
            ZipStreamSource zipFileSource = new ZipStreamSource(stream, false);
            zipFileSource.stream()
                    .parallel()
                    .filter(e -> !e.getEntry().isDirectory())
                    .forEach(this::zipConsumer);
        }
    }
    
    @Test
    public void bufferedMultiThreaded() throws IOException {
        try (FileInputStream stream = new FileInputStream(new File(FILENAME))) {
            ZipStreamSource zipStreamSource = new ZipStreamSource(stream, true);
            zipStreamSource.stream().parallel()
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
            assertTrue("io", false);
        }
    }
}
