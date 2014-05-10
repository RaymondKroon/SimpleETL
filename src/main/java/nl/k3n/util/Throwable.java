
package nl.k3n.util;

import java.util.function.Function;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class Throwable {
    @FunctionalInterface
    public interface CheckedRunnable {
        public void run() throws Exception;
    }
    
    public static Runnable unchecked(CheckedRunnable action) {
        return () -> {
            try {
                action.run();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
    
    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        public R apply (T in) throws Exception;
    }
    
    public static <T, R> Function<T, R> unchecked(CheckedFunction<T, R> func) {
        return (T obj) -> {
            try {
                return func.apply(obj);
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }
    
    public interface CheckedMethod<T> {
        public T call() throws Exception;
    }
    
    public static <T> T unchecked(CheckedMethod<T> method) {
        try {
            return method.call();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
