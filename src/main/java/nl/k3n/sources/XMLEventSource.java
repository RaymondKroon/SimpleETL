
package nl.k3n.sources;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;
import nl.k3n.interfaces.Source;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLEventSource implements Source<XMLEvent> {
    
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
    
    private final Stream<XMLEvent> innerStream;
    
    private Stream<XMLEvent> xmlToEventStreamMapper(InputStream src) throws IOException, XMLStreamException {
        
        XMLEventReader reader = factory.createXMLEventReader(src);
        
        return StreamSupport.stream(Spliterators.spliterator((Iterator<XMLEvent>)reader, 1, 
                Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }
    
    public XMLEventSource(InputStream xmlStream) throws IOException, XMLStreamException {
        
        XMLEventAllocator allocator = new XMLEventAllocatorImpl();
        getXMLInputFactory().setEventAllocator(allocator);
        
        innerStream = xmlToEventStreamMapper(xmlStream);
    }

    @Override
    public Stream<XMLEvent> stream() {
        return innerStream;
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public class XMLEventAllocatorImpl implements XMLEventAllocator {

        private final XMLEventFactory factory;
        
        public XMLEventAllocatorImpl() {
            factory = XMLEventFactory.newFactory();
        }
        
        @Override
        public XMLEventAllocator newInstance() {
            return this;
        }

        @Override
        public XMLEvent allocate(XMLStreamReader reader) throws XMLStreamException {
            if (reader.isStartElement()) {
                return factory.createStartElement("", "", reader.getLocalName());
            }
            else if (reader.isEndElement()) {
                return factory.createEndElement("", "", reader.getLocalName());
            }
            
            return null;
        }

        @Override
        public void allocate(XMLStreamReader reader, XMLEventConsumer consumer) throws XMLStreamException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
