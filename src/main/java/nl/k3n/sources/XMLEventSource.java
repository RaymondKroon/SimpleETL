
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
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.stream.util.XMLEventAllocator;
import javax.xml.stream.util.XMLEventConsumer;
import nl.k3n.transformers.FlatMap;
import nl.k3n.util.Wrappers;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLEventSource extends FlatMap<InputStream, XMLEvent> {
    
    private XMLInputFactory factory;
    
    private Stream<XMLEvent> xmlToEventStreamMapper(InputStream src) throws IOException, XMLStreamException {
        
        XMLEventReader reader = factory.createXMLEventReader(src);
        
        return StreamSupport.stream(Spliterators.spliterator((Iterator<XMLEvent>)reader, 1, 
                Spliterator.IMMUTABLE | Spliterator.NONNULL), false);
    }
    
    public XMLEventSource(Stream<InputStream> xmlFiles) {
        super(xmlFiles);
        this.factory = XMLInputFactory.newInstance();
        
        XMLEventAllocator allocator = new XMLEventAllocatorImpl();
        this.factory.setEventAllocator(allocator);
        
        
        this.mapper = Wrappers.uncheckedFunction(this::xmlToEventStreamMapper);
    }
    
    public class XMLEventAllocatorImpl implements XMLEventAllocator {

        private XMLEventFactory factory;
        
        public XMLEventAllocatorImpl() {
            this.factory = XMLEventFactory.newFactory();
        }
        
        @Override
        public XMLEventAllocator newInstance() {
            return this;
        }

        @Override
        public XMLEvent allocate(XMLStreamReader reader) throws XMLStreamException {
            if (reader.isStartElement()) {
                return this.factory.createStartElement("", "", reader.getLocalName());
            }
            else if (reader.isEndElement()) {
                return this.factory.createEndElement("", "", reader.getLocalName());
            }
            
            return null;
        }

        @Override
        public void allocate(XMLStreamReader reader, XMLEventConsumer consumer) throws XMLStreamException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
    }
}
