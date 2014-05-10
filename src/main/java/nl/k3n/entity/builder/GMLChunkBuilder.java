package nl.k3n.entity.builder;

import java.util.function.Consumer;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import nl.k3n.entity.GMLChunk;
import nl.k3n.util.XMLRebuilder;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class GMLChunkBuilder implements EntityBuilder<GMLChunk> {

    private boolean complete;

    private Consumer<XMLStreamReader> consumer;
    
    private XMLRebuilder rebuilder;

    public GMLChunkBuilder() {
        complete = false;
        consumer = this::waitForStart;
        
        rebuilder = new XMLRebuilder();
    }

    @Override
    public boolean isComplete() {
        return complete;
    }

    /**
     * Consumes until a complete gml block is found. Leaves stream at beginning
     * of element if done Consumes the whole stream if nog gml start element is
     * found. If started half way, returns invalid gml
     *
     * @param reader
     */
    @Override
    public void allocate(XMLStreamReader reader) {
        if (!complete) {
            consumer.accept(reader);
        }
        else {
            throw new IllegalStateException("Entity is complete");
        }
    }
    
    @Override
    public boolean consume(XMLStreamReader reader) throws IllegalStateException, XMLStreamException {
        consumer.accept(reader);
        while(!complete && reader.hasNext()) {
            reader.next();
            consumer.accept(reader);
        }
        
        return complete;
    }

    @Override
    public GMLChunkBuilder newInstance() {
        return new GMLChunkBuilder();
    }

    @Override
    public GMLChunk build() {
        return new GMLChunk(rebuilder.toString());
    }

    private void waitForStart(XMLStreamReader reader) {
        if (reader.isStartElement() && reader.getPrefix().equals("gml")) {
            rebuilder.rebuildElement(reader);

            consumer = this::appendTillEnd;
        }
    }

    private void appendTillEnd(XMLStreamReader reader) {
        if (reader.isStartElement() && !reader.getPrefix().equals("gml")) {
            complete = true;
        } else {
            rebuilder.rebuildElement(reader);
        }
    }

}
