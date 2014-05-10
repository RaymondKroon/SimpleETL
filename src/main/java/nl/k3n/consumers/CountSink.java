
package nl.k3n.consumers;

import java.util.function.Consumer;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class CountSink<T> implements Consumer<T> {

    private int count = 0;
    private final int logRate;
    
    public CountSink(int logRate) {
        this.logRate = logRate;
    }

    @Override
    public synchronized void accept(T t) {
        count++;
        if (count % logRate == 0) {
            System.out.println("Parsed: " + count);
        }
    }
    
}
