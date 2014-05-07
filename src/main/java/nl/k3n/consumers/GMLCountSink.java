
package nl.k3n.consumers;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.function.Consumer;
import nl.k3n.entity.GMLChunk;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class GMLCountSink implements Consumer<GMLChunk> {

    private Multiset<String> counts;
    private int count = 0;
    
    public GMLCountSink() {
        this.counts = HashMultiset.create();
    }

    @Override
    public synchronized void accept(GMLChunk t) {
        count++;
        if (count % 100000 == 0) {
            System.out.println("Parsed: " + count);
        }
    }
    
}
