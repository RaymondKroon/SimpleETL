
package nl.k3n.util;

import java.util.function.Function;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class Wrappers {
    @FunctionalInterface
    public interface CheckedRunnable {
        public void run() throws Exception;
    }
    
    public static Runnable uncheckedAction(CheckedRunnable action) {
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
    
    public static <T, R> Function<T, R> uncheckedFunction(CheckedFunction<T, R> func) {
        return new Function<T, R> () {
            @Override
            public R apply(T obj) {
                try {
                    return func.apply(obj);
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        };
    }
}
