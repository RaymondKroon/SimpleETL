package nl.k3n;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nl.k3n.consumers.CopyToPostgres;
import nl.k3n.consumers.CountSink;
import nl.k3n.flatmap.XMLStreamToGeometries;
import nl.k3n.flatmap.ZipEntriesFromZipEntry;
import nl.k3n.interfaces.Source;
import nl.k3n.sources.ZipFileSource;
import static nl.k3n.util.Throwable.*;
import nl.k3n.zip.SourcedZipEntry;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.deegree.feature.types.AppSchema;
import org.deegree.geometry.io.WKBWriter;
import org.deegree.gml.GMLVersion;
import org.deegree.gml.schema.GMLAppSchemaReader;
import org.postgresql.PGConnection;
import org.postgresql.ds.PGSimpleDataSource;

public class SimpleETL {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        
        System.setProperty("stax.inputfactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("stax.outputfactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        
        Options options = new Options();
        options.addOption("h", "help", false, "display help");
        
        CommandLineParser parser = new GnuParser();
        
        try {
            CommandLine line = parser.parse(options, args);
            
            if (line.hasOption("h") || line.getArgs().length < 1) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("simpleETL [OPTIONS] [INPUT]", options);
                return;
            }
            else {
                String fileName = line.getArgs()[0];
                
                GMLAppSchemaReader appSchemaReader = new GMLAppSchemaReader(GMLVersion.GML_31, null, "http://www.opengis.net/gml");
                AppSchema appSchema = appSchemaReader.extractAppSchema();
                
                Predicate<SourcedZipEntry> zipFilter = (SourcedZipEntry entry) -> {
                    return entry.getEntry().getName().toLowerCase().endsWith(".zip");
                };
                
                Predicate<SourcedZipEntry> xmlFilter = (SourcedZipEntry entry) -> {
                    return entry.getEntry().getName().toLowerCase().endsWith(".xml");
                };
                
                PGSimpleDataSource dataSource = new PGSimpleDataSource();
                dataSource.setDatabaseName("gisdb");
                dataSource.setUser("postgres");
                dataSource.setPassword("postgres");
                
                try (Source<SourcedZipEntry> src = new ZipFileSource(new File(fileName))) {
                    
                    Stream<byte[]> pipeline = src.stream().unordered()
                            .filter(zipFilter)
                            .flatMap(new ZipEntriesFromZipEntry())
                            .filter(xmlFilter)
                            .map(c -> unchecked(c::getData))
                            .flatMap(new XMLStreamToGeometries(appSchema))
                            .map(g -> unchecked(() -> WKBWriter.write(g)))
                            .parallel();
                    
                    //CountSink sink = new CountSink(100000);
                    
                    CopyToPostgres copy = new CopyToPostgres((PGConnection)unchecked(() -> dataSource.getConnection()), 
                            "bag_copy_test.geometry", 500000, 100000);
                    
                    pipeline.forEach(copy);
                    
                    // flush remainder
                    unchecked(copy::flush);
                    
                }
                catch (FileNotFoundException ex) {
                    System.err.println("File not found.");
                }
                catch (IOException ex) {
                    System.err.println("Transform exception. Reason: " + ex.getMessage());
                }
            }
            
            
        } catch (ParseException ex) {
            System.err.println("Parsing failed. Reason: " + ex.getMessage());
        }
        
    }
}
