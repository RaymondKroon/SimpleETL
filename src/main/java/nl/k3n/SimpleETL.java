package nl.k3n;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import nl.k3n.aggregators.XMLChunk;
import nl.k3n.aggregators.XMLChunkAggregator;
import nl.k3n.interfaces.Sink;
import nl.k3n.interfaces.Source;
import nl.k3n.sinks.CountSink;
import nl.k3n.sources.DoubleZipSource;
import nl.k3n.sources.XMLEventSource;
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
                
                
                
                Predicate<ZipEntry> xmlFilter = (ZipEntry entry) -> {
                    return entry.getName().toLowerCase().endsWith(".xml");
                };
                
                try (Source<InputStream> src = new DoubleZipSource(new File(fileName), xmlFilter)) {
                    
                    XMLEventSource eventStream = new XMLEventSource(src.stream());
                    XMLChunkAggregator aggregator = XMLChunkAggregator.BAGAggregator(eventStream.stream());
                    Sink<XMLChunk> sink = new CountSink(aggregator.stream());
                    
                    sink.run();
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
