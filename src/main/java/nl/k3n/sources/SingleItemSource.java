
package nl.k3n.sources;

import com.google.common.collect.Iterators;
import java.io.IOException;
import java.util.Iterator;
import nl.k3n.interfaces.Source;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class SingleItemSource<T> implements Source<T> {

    Iterator<T> iterator;
    
    public SingleItemSource(T item) {
        this.iterator = Iterators.forArray(item);
    }
    
    @Override
    public Iterator<T> iterator() {
        return this.iterator;
    }

    @Override
    public void close() throws IOException {
        // nothing
    }
    
}
