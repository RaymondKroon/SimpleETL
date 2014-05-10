
package nl.k3n.aggregators;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import nl.k3n.entity.builder.EntityBuilder;
import nl.k3n.interfaces.Source;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLStreamAggregator<T> implements Source<T> {
    
    private final static Object staticLock = new Object();
    
    private static XMLInputFactory factory;
    
    private static XMLInputFactory getXMLInputFactory() {
        synchronized(staticLock) {
            if (factory == null) {
                factory = XMLInputFactory.newFactory("stax.inputfactory", null);
            }
            return factory;
        }
    }
    
    private final EntityBuilder<T> builder;
    private final XMLStreamReader reader;
    private final Stream<T> innerStream;
    
    private Stream<T> createStream() {
        
        EntityIterator iterator = new EntityIterator();
        
        return StreamSupport.stream(Spliterators.spliterator(iterator, 1, 
                Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }
    
    public XMLStreamAggregator(InputStream xmlStream, EntityBuilder<T> builder) throws XMLStreamException  {
        this.builder = builder;
        this.reader = getXMLInputFactory().createXMLStreamReader(xmlStream);
        this.innerStream = createStream();
    }

    @Override
    public Stream<T> stream() {
        return innerStream;
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public class EntityIterator implements Iterator<T> {
        
        private T next = null;
        private EntityBuilder<T> privateBuilder = builder.newInstance();
        
        @Override
        public boolean hasNext() {
            try {
                
                if (privateBuilder.consume(reader)) {
                    next = privateBuilder.build();
                    privateBuilder = builder.newInstance();
                    return true;
                }
                else {
                    return false;
                }
            } catch (XMLStreamException ex) {
                return false;
            }
        }

        @Override
        public T next() {
            if (next == null) {
                if (hasNext()) {
                    return next;
                }
                return null;
            }
            return next;
        }
        
    }
}
