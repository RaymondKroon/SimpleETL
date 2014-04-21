package nl.k3n.interfaces;

import java.io.Closeable;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 * @param <T>
 */
public interface Sink<T> extends Runnable, Closeable {
}
