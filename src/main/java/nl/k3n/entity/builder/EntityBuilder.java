
package nl.k3n.entity.builder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public interface EntityBuilder<T> {
    boolean isComplete();
    
    /**
     * allocates exactly one element. Does not change state of the reader.
     * @param reader 
     */
    void allocate(XMLStreamReader reader) throws IllegalStateException;
    
    /**
     * Monsumes the stream until done or consumed.
     * May change the state
     * @param reader
     * @returns complete or not
     * @throws IllegalStateException 
     */
    boolean consume(XMLStreamReader reader) throws IllegalStateException, XMLStreamException;
    
    /**
     * returns a new builder. Should be threadsafe
     * @return 
     */
    EntityBuilder<T> newInstance();
    
    /**
     * returns entity;
     * @return 
     */
    T build();
    
}
