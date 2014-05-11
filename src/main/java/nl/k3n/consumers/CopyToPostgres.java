package nl.k3n.consumers;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.function.Consumer;
import nl.k3n.util.StringUtils;
import static nl.k3n.util.Throwable.*;
import org.postgresql.PGConnection;
import org.postgresql.copy.CopyManager;

/**
 * Copies to DB in batches. Not sure this is the way to go... (state in
 * consumer)
 *
 * @author Raymond Kroon <raymond@k3n.nl>
 */
public class CopyToPostgres implements Consumer<byte[]> {

    private final byte[][] batched;
    private int batchCount;
    private final int batchSize;
    private final PGConnection connection;
    private final String table;
    
    private int count = 0;
    private int logRate;

    public CopyToPostgres(PGConnection connection, String table, int batchSize, int logRate) {
        this.batchSize = batchSize;
        this.batched = new byte[batchSize][];
        this.batchCount = 0;

        this.connection = connection;
        this.table = table;
        
        this.logRate = logRate;
    }

    @Override
    public synchronized void accept(byte[] t) {
        batched[batchCount] = t;
        batchCount++;
        if (batchCount == batchSize) {
            unchecked(this::flush).run();
        }
        
        count++;
        if (count % logRate == 0) {
            System.out.println("Parsed: " + count);
        }
    }

    public void flush() throws SQLException, IOException {
        StringBuilder sb = new StringBuilder();
        CopyManager cpManager = connection.getCopyAPI();
        for (int i = 0; i < batchCount; i++) {
            sb.append(StringUtils.bytesToHex(batched[i])).append("\n");
        }
        Reader reader = new StringReader(sb.toString());
        cpManager.copyIn("COPY " + table + " FROM STDIN WITH CSV", reader);
        batchCount = 0;
    }

}
