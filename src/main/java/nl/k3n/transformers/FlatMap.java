
package nl.k3n.transformers;

import java.io.IOException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import nl.k3n.interfaces.Source;
import nl.k3n.spliterator.FixedSizeIteratorSpliterator;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 * @param <I>
 * @param <O>
 */
public class FlatMap<I, O> implements Iterator<O>, Source<O> {
    
    private Spliterator<I> src;
    protected Function<I, ? extends Stream<O>> mapper;
    private Spliterator<O> mappedSrc;
    
    private O currentOutput;
    
    private Stream<O> innerStream;
    
    private static <I> Stream emptyMapper(I input) {
        return Stream.empty();
    }
    
    public FlatMap (Stream<I> src) {
        this(src, FlatMap::emptyMapper);
    }
    
    public FlatMap(Stream<I> src, Function<I, ? extends Stream<O>> mapper) {
        if (src == null) {
            throw new NullPointerException("src");
        }
        
        if (mapper == null) {
            throw new NullPointerException("mapper");
        }
        
        this.src = src.spliterator();
        this.mapper = mapper;
        
        this.innerStream = StreamSupport.stream(new FixedSizeIteratorSpliterator<>(this, Spliterator.IMMUTABLE | Spliterator.NONNULL, 10)
            , false);
        
    }

    private void nothing(I in) {}
    
    private void updateMappedSrc(I in) {
        this.mappedSrc = this.mapper.apply(in).spliterator();
    }
    
    private void setCurrentOutput(O in) {
        this.currentOutput = in;
    }
    
    @Override
    public boolean hasNext() {
        if (this.mappedSrc == null && !this.src.tryAdvance(this::updateMappedSrc)) {
            return false;
        } 
        else if (this.mappedSrc.tryAdvance(this::setCurrentOutput)) {
            return true;
        }
        else if (this.src.tryAdvance(this::updateMappedSrc)) {
            return this.mappedSrc.tryAdvance(this::setCurrentOutput);
        }
        else {
            return false;
        }
    }

    @Override
    public O next() {
        return this.currentOutput;
    }

    @Override
    public Stream<O> stream() {
        return innerStream;
    }

    @Override
    public void close() throws IOException {
        // TODO
    }
}
