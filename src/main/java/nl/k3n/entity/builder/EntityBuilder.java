
package nl.k3n.entity.builder;

import javax.xml.stream.XMLStreamReader;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public interface EntityBuilder<T> {
    boolean isComplete();
    
    /**
     * Consumes stream until done.
     * Implementation should state what the begin and endconditions are
     * @param reader 
     */
    void consume(XMLStreamReader reader);
    
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
