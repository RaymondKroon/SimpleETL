
package nl.k3n.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class UncloseableBufferedInputStream extends BufferedInputStream {

    public UncloseableBufferedInputStream(InputStream in) {
        super(in);
    }

    public UncloseableBufferedInputStream(InputStream in, int size) {
        super(in, size);
    }

    @Override
    public void close() throws IOException {
        //nothing;
    }
    
}
