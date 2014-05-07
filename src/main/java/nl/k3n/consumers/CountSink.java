
package nl.k3n.consumers;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import java.util.function.Consumer;
import nl.k3n.aggregators.XMLChunk;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class CountSink implements Consumer<XMLChunk> {

    private Multiset<String> counts;
    private int count = 0;
    
    public CountSink() {
        this.counts = HashMultiset.create();
    }
    
    public void printStatistics() {
        
        System.out.println("Total features: " + count);
        System.out.println(counts);
    }

    @Override
    public synchronized void accept(XMLChunk t) {
        count++;
        if (count % 100000 == 0) {
            System.out.println("Parsed: " + count);
        }
        counts.add(t.Name.getLocalPart());
    }
    
}
