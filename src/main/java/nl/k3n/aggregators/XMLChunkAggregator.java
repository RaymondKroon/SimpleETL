
package nl.k3n.aggregators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLChunkAggregator implements Iterator<XMLChunk> {

    private final Predicate<StartElement> filter;
    private final Spliterator<XMLEvent> src;
    
    private XMLChunk nextChunk = null;
    private boolean chunkIsComplete = false;
    private Consumer<XMLEvent> parse;
    
    private Stream<XMLChunk> innerStream;
    
    public static XMLChunkAggregator BAGAggregator(Stream<XMLEvent> src) {
        
        Predicate<StartElement> filter = (StartElement e) -> {
            
            List<String> allowedElements = Arrays.asList(
                    "Ligplaats", 
                    "Nummeraanduiding", 
                    "OpenbareRuimte",
                    "Pand",
                    "Standplaats",
                    "Verblijfsobject",
                    "Woonplaats");
            
            String elementName = e.getName().getLocalPart();
            return allowedElements.contains(elementName);
        };
        
        return new XMLChunkAggregator(src, filter);
    }
    
    public XMLChunkAggregator(Stream<XMLEvent> src, Predicate<StartElement> startElementFilter) {
        this.filter = startElementFilter;
        this.src = src.spliterator();
        this.parse = this::waitForStart;
        
        this.innerStream = StreamSupport.stream(Spliterators.spliterator(this, 1, 
                Spliterator.CONCURRENT | Spliterator.DISTINCT | Spliterator.IMMUTABLE | Spliterator.NONNULL), true);
    }

    @Override
    public boolean hasNext() {
        while (this.src.tryAdvance(this.parse) && !this.chunkIsComplete) {
            
        }
        
        return chunkIsComplete;
    }

    @Override
    public XMLChunk next() {
        try {
            return nextChunk;
        }
        finally {
            this.nextChunk = null;
            this.chunkIsComplete= false;
        }
    }
    
    private void waitForStart(XMLEvent event) {
        if (event != null && event.isStartElement() )
        {
            QName elementName = event.asStartElement().getName();
            if (event.isStartElement() && this.filter.test(event.asStartElement())) {
                this.nextChunk = new XMLChunk(elementName);
                this.nextChunk.Elements.add(event);
                this.chunkIsComplete = false;

                this.parse = this::parseInner;
            }
        }
    }
    
    private void parseInner(XMLEvent event) {
        if (event != null && event.isEndElement()
                && event.asEndElement().getName().equals(this.nextChunk.Name)) 
        {
            this.nextChunk.Elements.add(event);
            this.chunkIsComplete = true;
            this.parse = this::waitForStart;
        }
        else {
            this.nextChunk.Elements.add(event);
        }
    }

    public Stream<XMLChunk> stream() {
        return this.innerStream;
    }
}
