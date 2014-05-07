package nl.k3n;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Stream;
import nl.k3n.aggregators.XMLChunk;
import nl.k3n.consumers.CountSink;
import nl.k3n.flatmap.XmlStreamToXmlChunks;
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

public class SimpleETL {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        System.setProperty("stax.inputfactory", "com.fasterxml.aalto.stax.InputFactoryImpl");
        
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
                
                
                
                Predicate<SourcedZipEntry> zipFilter = (SourcedZipEntry entry) -> {
                    return entry.getEntry().getName().toLowerCase().endsWith(".zip");
                };
                
                Predicate<SourcedZipEntry> xmlFilter = (SourcedZipEntry entry) -> {
                    return entry.getEntry().getName().toLowerCase().endsWith(".xml");
                };
                
                try (Source<SourcedZipEntry> src = new ZipFileSource(new File(fileName))) {
                    
                    Stream<XMLChunk> pipeline = src.stream()
                            .filter(zipFilter)
                            .flatMap(new ZipEntriesFromZipEntry())
                            .filter(xmlFilter)
                            .map(c -> unchecked(c::getData))
                            .flatMap(new XmlStreamToXmlChunks())
                            .parallel();
                    
                    CountSink sink = new CountSink();
                    pipeline.forEach(sink);
                    
                    sink.printStatistics();
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
