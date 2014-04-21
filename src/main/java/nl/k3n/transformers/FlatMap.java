
package nl.k3n.transformers;

import java.io.IOException;
import java.util.Iterator;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.k3n.interfaces.Source;
import nl.k3n.interfaces.Transformer;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 * @param <I>
 * @param <O>
 */
public class FlatMap<I, O> implements Transformer<I, O>, Source<O>, Iterator<O> {

    private Source<I> src;
    private Function<I, ? extends Source<O>> mapper;
    private Source<O> mappedSrc;
    
    public FlatMap(Source<I> src, Function<I, ? extends Source<O>> mapper) {
        if (src == null) {
            throw new NullPointerException("src");
        }
        
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        
        this.src = src;
        this.mapper = mapper;
        
    }
    
    @Override
    public Iterator<O> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        if (this.mappedSrc == null && !this.src.iterator().hasNext()) {
            return false;
        } 
        else if (this.mappedSrc == null && this.src.iterator().hasNext()) {
            this.mappedSrc = this.mapper.apply(this.src.iterator().next());
            return this.mappedSrc.iterator().hasNext();
        }
        else if (this.mappedSrc.iterator().hasNext()) {
            return true;
        }
        else if (this.src.iterator().hasNext()) {
            try {
                this.mappedSrc.close();
            } catch (IOException ex) {
                //TODO:
            }
            this.mappedSrc = this.mapper.apply(this.src.iterator().next());
            return this.mappedSrc.iterator().hasNext();
        }
        else {
            return false;
        }
    }

    @Override
    public O next() {
        return this.mappedSrc.iterator().next();
    }

    @Override
    public void close() throws IOException {
        if (this.mappedSrc != null) {
            this.mappedSrc.close();
        }
    }
}
