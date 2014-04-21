
package nl.k3n.aggregators;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.xml.namespace.QName;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import nl.k3n.interfaces.Aggregator;
import nl.k3n.interfaces.Source;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class XMLChunkAggregator implements Aggregator<XMLEvent, XMLChunk>, Iterator<XMLChunk> {

    private final Predicate<StartElement> filter;
    private final Iterable<XMLEvent> src;
    
    private XMLChunk nextChunk = null;
    private boolean chunkIsComplete = false;
    private Consumer<XMLEvent> parse;
    
    public static XMLChunkAggregator BAGAggregator(Source<XMLEvent> src) {
        
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
    
    public XMLChunkAggregator(Iterable<XMLEvent> src, Predicate<StartElement> startElementFilter) {
        this.filter = startElementFilter;
        this.src = src;
        this.parse = this::waitForStart;
    }
    
    @Override
    public Iterator<XMLChunk> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        while (this.src.iterator().hasNext() && !this.chunkIsComplete) {
            this.parse.accept(this.src.iterator().next());
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
        if (event.isStartElement() )
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
        if (event.isEndElement()
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

    @Override
    public void close() throws IOException {
        // nothing
    }
}
