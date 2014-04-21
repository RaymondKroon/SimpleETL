
package nl.k3n.sinks;

import java.io.IOException;
import nl.k3n.aggregators.XMLChunk;
import nl.k3n.interfaces.Sink;
import nl.k3n.interfaces.Source;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class LogSink implements Sink<XMLChunk> {

    private final Iterable<XMLChunk> src;
    
    public LogSink(Iterable<XMLChunk> src) {
        this.src = src;
    }
    
    @Override
    public void run() {
        src.forEach(
                (c) -> System.out.println(c.Elements.size())
        );
    }

    @Override
    public void close() throws IOException {
    }
    
}
