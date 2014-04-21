
package nl.k3n.sinks;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.io.IOException;
import nl.k3n.aggregators.XMLChunk;
import nl.k3n.interfaces.Sink;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class CountSink implements Sink<XMLChunk> {

    private final Iterable<XMLChunk> src;
    private Multiset<String> counts;
    
    public CountSink(Iterable<XMLChunk> src) {
        this.src = src;
        this.counts = HashMultiset.create();
    }
    
    @Override
    public void run() {
        int count = 0;
        for (XMLChunk chunk : src) {
            count++;
            if (count % 100000 == 0) {
                System.out.println("Parsed: " + count);
            }
            counts.add(chunk.Name.getLocalPart());
        }
        
        System.out.println("Total features: " + count);
        System.out.println(counts);
    }

    @Override
    public void close() throws IOException {
    }
    
}
