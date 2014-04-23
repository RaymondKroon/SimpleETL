
package nl.k3n.sinks;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.io.IOException;
import java.util.stream.Stream;
import nl.k3n.aggregators.XMLChunk;
import nl.k3n.interfaces.Sink;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class CountSink implements Sink<XMLChunk> {

    private final Stream<XMLChunk> src;
    private Multiset<String> counts;
    private int count = 0;
    
    public CountSink(Stream<XMLChunk> src) {
        this.src = src;
        this.counts = HashMultiset.create();
    }
    
    @Override
    public void run() {
        src.forEach(this::counter);
        
        System.out.println("Total features: " + count);
        System.out.println(counts);
    }
    
    private void counter(XMLChunk chunk) {
        count++;
        if (count % 100000 == 0) {
            System.out.println("Parsed: " + count);
        }
        counts.add(chunk.Name.getLocalPart());
    }

    @Override
    public void close() throws IOException {
    }
    
}
