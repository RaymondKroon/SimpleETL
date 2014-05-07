
package nl.k3n.performance.gdal;

import org.gdal.gdal.gdal;
import org.gdal.ogr.Geometry;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
@Ignore
public class GDALtest {
    
    public static String GML = "<gml:Polygon srsName=\"urn:ogc:def:crs:EPSG::28992\"><gml:exterior><gml:LinearRing><gml:posList srsDimension=\"3\" count=\"17\">254059.737 593504.637 0.0 254059.227 593500.0 0.0 254059.216 593499.899 0.0 254058.242 593500.0 0.0 254057.914 593500.034 0.0 254057.893 593500.0 0.0 254057.807 593499.863 0.0 254052.074 593490.692 0.0 254052.182 593490.639 0.0 254058.138 593490.002 0.0 254057.932 593488.165 0.0 254074.487 593486.413 0.0 254075.981 593500.0 0.0 254076.265 593502.583 0.0 254076.561 593502.55 0.0 254076.594 593502.847 0.0 254059.737 593504.637 0.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon>";
    
    public GDALtest() {
    }
    
    @Before
    public void setUp() {
    }
    
    @BeforeClass 
    public static void setUpTests() {
        //gdal.AllRegister();
    }

    @Test
    public void parseGML() {
        
        Geometry geom = Geometry.CreateFromGML(GML);
        
        String geoJson = geom.ExportToJson();
        
        assertNotNull(geoJson);
    }
    
   
}
