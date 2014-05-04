
package nl.k3n.spliterator;

import java.util.Iterator;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class FixedSizeIteratorSpliterator<T> implements Spliterator<T> {

    private final Iterator<T> iterator;
    private final int characteristics;
    private final int batchSize;
    
    public FixedSizeIteratorSpliterator(Iterator<T> iterator, int characteristics, int batchSize) {
        Objects.requireNonNull(iterator);
        this.iterator = iterator;
        this.characteristics = characteristics;
        this.batchSize = batchSize;
        
    }
    
    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (action == null) throw new NullPointerException();
            if (iterator.hasNext()) {
                action.accept(iterator.next());
                return true;
            }
            return false;
    }

    @Override
    public Spliterator<T> trySplit() {
        Object[] a = new Object[batchSize];
        int i = 0;
        while (i < batchSize && iterator.hasNext() ) {
            a[i] = iterator.next();
            i++;
        }
        
        return Spliterators.spliterator(a, 0, i, characteristics);
    }

    @Override
    public long estimateSize() {
        return iterator.hasNext() ? Long.MAX_VALUE : 0;
    }

    @Override
    public int characteristics() {
        return characteristics;
    }
    
}
