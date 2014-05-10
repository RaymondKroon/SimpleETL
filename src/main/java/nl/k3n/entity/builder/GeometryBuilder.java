
package nl.k3n.entity.builder;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.deegree.commons.xml.XMLParsingException;
import org.deegree.commons.xml.stax.XMLStreamReaderWrapper;
import org.deegree.cs.exceptions.UnknownCRSException;
import org.deegree.feature.types.AppSchema;
import org.deegree.geometry.Geometry;
import org.deegree.gml.GMLInputFactory;
import org.deegree.gml.GMLStreamReader;
import org.deegree.gml.GMLVersion;
import org.deegree.gml.geometry.GML3GeometryReader;
import org.deegree.gml.geometry.GMLGeometryReader;


public class GeometryBuilder implements EntityBuilder<Geometry> {
    
    private boolean complete;
    private Geometry result;
    
    private GMLGeometryReader gmlGeometryReader;
    private AppSchema appSchema;
    
    public GeometryBuilder(AppSchema appSchema) {
        this(createGMLReader(appSchema));
    }
    
    public GeometryBuilder(GMLStreamReader gmlStreamReader) {
        complete = false;
        this.appSchema = gmlStreamReader.getAppSchema();
        this.gmlGeometryReader = new GML3GeometryReader(gmlStreamReader);
    }
    
    private static GMLStreamReader createGMLReader(AppSchema appSchema) {
        try {
            GMLStreamReader reader = GMLInputFactory.createGMLStreamReader(GMLVersion.GML_2, (XMLStreamReader) null);
            
            reader.setApplicationSchema(appSchema);
            return reader;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public boolean isComplete() {
        return complete;
    }

    //<gml:Polygon srsName="urn:ogc:def:crs:EPSG::28992"><gml:exterior><gml:LinearRing><gml:posList srsDimension="3" count="17">254059.737 593504.637 0.0 254059.227 593500.0 0.0 254059.216 593499.899 0.0 254058.242 593500.0 0.0 254057.914 593500.034 0.0 254057.893 593500.0 0.0 254057.807 593499.863 0.0 254052.074 593490.692 0.0 254052.182 593490.639 0.0 254058.138 593490.002 0.0 254057.932 593488.165 0.0 254074.487 593486.413 0.0 254075.981 593500.0 0.0 254076.265 593502.583 0.0 254076.561 593502.55 0.0 254076.594 593502.847 0.0 254059.737 593504.637 0.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon>
    @Override
    public void allocate(XMLStreamReader reader) throws IllegalStateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    
    @Override
    public boolean consume(XMLStreamReader reader) throws IllegalStateException, XMLStreamException {
        
        while (!gmlGeometryReader.isGeometryElement(reader) && reader.hasNext()) {
            reader.next();
        }
        
        if (!reader.hasNext()) {
            return false;
        }
        
        try {
            result = gmlGeometryReader.parse(new XMLStreamReaderWrapper(reader, null));
            complete = true;
            
        } catch (XMLParsingException | UnknownCRSException ex) {
            throw new XMLStreamException(ex);
        }
        
        return complete;
    }

    @Override
    public EntityBuilder<Geometry> newInstance() {
        return new GeometryBuilder(appSchema);
    }

    @Override
    public Geometry build() {
        return result;
    }
    
}
