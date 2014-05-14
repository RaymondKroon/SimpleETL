package nl.k3n;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.function.Predicate;
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
import org.apache.commons.cli.Option;
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
    public static void main(String[] args) throws ClassCastException, ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        
        System.setProperty("stax.inputfactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        System.setProperty("stax.outputfactory", "com.fasterxml.aalto.stax.OutputFactoryImpl");
        
        Options options = new Options();
        options.addOption("h", "help", false, "display help");
        options.addOption("c", "connectionstring", true, "database connection string");
        options.addOption("u", "user", true, "database connection username");
        options.addOption("p", "password", true, "database connection password");
        options.addOption("T", "test", false, "do a test run without database inserts");
        Option fileOption = new Option("f", "file", true, "file to process");
        fileOption.setRequired(true);
        options.addOption(fileOption);
        
        CommandLineParser parser = new GnuParser();
        
        try {
            CommandLine line = parser.parse(options, args);
            HelpFormatter formatter = new HelpFormatter();
            if (line.hasOption("h")) {
                formatter.printHelp("simpleETL [OPTIONS]", options);
                return;
            }
            else {
                String filename = line.getOptionValue("f").trim();
                System.out.println(filename);
                
                boolean testRun = false;
                
                if (!(line.hasOption("c") || line.hasOption("T"))) {
                    System.err.println("You should provide either a database connectionString or run a test run");
                    return;
                }
                
                if (line.hasOption("T")) {
                    testRun = true;
                }
                
                
                // maps to local xsd so no download
                GMLAppSchemaReader appSchemaReader = new GMLAppSchemaReader(GMLVersion.GML_31, null, "http://schemas.opengis.net/gml/3.1.1/base/gml.xsd");
                AppSchema appSchema = appSchemaReader.extractAppSchema();
                
                Predicate<SourcedZipEntry> zipFilter = (SourcedZipEntry entry) -> {
                    return entry.getEntry().getName().toLowerCase().endsWith(".zip");
                };
                
                Predicate<SourcedZipEntry> xmlFilter = (SourcedZipEntry entry) -> {
                    return entry.getEntry().getName().toLowerCase().endsWith(".xml");
                };
                
                try (Source<SourcedZipEntry> src = new ZipFileSource(new File(filename))) {
                    
                    Stream<byte[]> pipeline = src.stream().unordered()
                            .filter(zipFilter)
                            .flatMap(new ZipEntriesFromZipEntry())
                            .filter(xmlFilter)
                            .map(c -> unchecked(c::getData))
                            .flatMap(new XMLStreamToGeometries(appSchema))
                            .map(g -> unchecked(() -> WKBWriter.write(g)))
                            .parallel();
                    
                    //CountSink sink = new CountSink(100000);
                    
                    if (!testRun) {
                    
                        PGSimpleDataSource dataSource = new PGSimpleDataSource();
                        
                        //dataSource.setUrl("jdbc:postgresql://localhost:5432/gisdb");
                        dataSource.setUrl(line.getOptionValue("c"));
                        if (line.hasOption("u")) {
                            dataSource.setUser(line.getOptionValue("u"));
                        }
                        
                        if (line.hasOption("p")) {
                            dataSource.setPassword(line.getOptionValue("p"));
                        }
                        
                        CopyToPostgres copy = new CopyToPostgres((PGConnection)unchecked(() -> dataSource.getConnection()), 
                                "bag_copy_test.geometry", 100000, 4, 100000);

                        pipeline.forEach(copy);

                        // flush remainder
                        copy.waitForFlush();
                    }
                    else {
                        CountSink count = new CountSink(100000);
                        pipeline.forEach(count);
                    }
                    
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
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("simpleETL [OPTIONS]", options);
        }
        
    }
}
