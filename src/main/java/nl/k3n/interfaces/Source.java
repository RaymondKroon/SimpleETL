
package nl.k3n.interfaces;

import java.io.Closeable;
import java.util.stream.Stream;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public interface Source<T> extends Closeable {
    Stream<T> stream();
}
