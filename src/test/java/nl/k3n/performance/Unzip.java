
package nl.k3n.performance;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalTime;
import java.util.Date;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Test;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class Unzip {
    
    public static String FILENAME = "D:\\Downloads\\inspireadressen.zip";
    
    private final static Logger LOGGER = Logger.getLogger(Unzip.class.getName());
    
    public Unzip() {
    }

    @Test
    public void singleThreaded() throws IOException {
        ZipFile zipFile = new ZipFile(FILENAME);
        
        zipFile.stream().filter(e -> !e.isDirectory()).forEach(zipConsumer(zipFile));
        
        zipFile.close();
    }
    
    @Test
    public void multiThreaded() throws IOException {
        ZipFile zipFile = new ZipFile(FILENAME);
        
        zipFile.stream().parallel().filter(e -> !e.isDirectory()).forEach(zipConsumer(zipFile));
        
        zipFile.close();
    }
    
    private Consumer<ZipEntry> zipConsumer(final ZipFile zip) {
        return new Consumer<ZipEntry>() {
            @Override
            public void accept(ZipEntry t) {
                try {
                    LocalTime start = LocalTime.now();
                    
                    InputStream stream = zip.getInputStream(t);
                    OutputStream target = new ByteArrayOutputStream();
                    ByteStreams.copy(stream, target);
                    
                    LocalTime stop = LocalTime.now();
                    System.out.format("%s: %s%n - %s%n", t.getName(), start, stop);
                    
                    // and forget about it :)
                } catch (IOException ex) {
                    Logger.getLogger(Unzip.class.getName()).log(Level.SEVERE, null, ex);
                }
            }  
        };
    }
}
