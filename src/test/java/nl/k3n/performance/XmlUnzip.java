
package nl.k3n.performance;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.k3n.sources.SourcedZipEntry;
import nl.k3n.sources.ZipFileSource;
import org.junit.Test;

/**
 * Use a zip with xml files
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XmlUnzip {
    
    public static String FILENAME = "D:\\Downloads\\inspireadressen\\9999PND08032014.zip";
    
    public XmlUnzip() {
    }

    @Test
    public void singleThreaded() throws IOException {
        try (ZipFileSource zipFileSource = new ZipFileSource(new File(FILENAME))) {
            zipFileSource.stream()
                    .filter(e -> !e.getEntry().isDirectory())
                    .forEach(this::zipConsumer);
        }
    }
    
    @Test
    public void multiThreaded() throws IOException {
        try (ZipFileSource zipFileSource = new ZipFileSource(new File(FILENAME))) {
            zipFileSource.stream().parallel()
                    .filter(e -> !e.getEntry().isDirectory())
                    .forEach(this::zipConsumer);
        }
    }
    
    private void zipConsumer(SourcedZipEntry entry) {
        try {
            LocalTime start = LocalTime.now();
            
            OutputStream target = new ByteArrayOutputStream();
            ByteStreams.copy(entry.getData(), target);

            LocalTime stop = LocalTime.now();
            System.out.format("%s: %s%n - %s%n", entry.getEntry().getName(), start, stop);

            // and forget about it :)
        } catch (IOException ex) {
            Logger.getLogger(ZipContainerUnzip.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
