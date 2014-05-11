package nl.k3n.consumers;

import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
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

    private ForkJoinPool pool;

    public CopyToPostgres(PGConnection connection, String table, int batchSize, int logRate) {
        this.batchSize = batchSize;
        this.batched = new byte[batchSize][];
        this.batchCount = 0;

        this.connection = connection;
        this.table = table;

        this.logRate = logRate;

        this.pool = new ForkJoinPool(1);
    }

    @Override
    public synchronized void accept(byte[] t) {
        count++;
        if (count % logRate == 0) {
            System.out.println("Parsed: " + count);
        }

        batched[batchCount] = t;
        batchCount++;
        if (batchCount == batchSize) {
            unchecked(this::flush).run();
        }
    }

    public void flush() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < batchCount; i++) {
            sb.append(StringUtils.bytesToHex(batched[i])).append("\n");
        }

        Runnable copy = copyAction(sb.toString());
        pool.execute(copy);
        
        batchCount = 0;

    }
    
    public void waitForFlush() {
        flush();
        System.out.println("Waiting for " + pool.getQueuedSubmissionCount() + " + " + pool.getQueuedTaskCount() + " tasks to complete");
        pool.awaitQuiescence(10, TimeUnit.MINUTES);
    }

    private Runnable copyAction(String data) {
        return unchecked(() -> {
            CopyManager cpManager = connection.getCopyAPI();
            Reader reader = new StringReader(data);
            cpManager.copyIn("COPY " + table + " FROM STDIN WITH CSV", reader);
        });
    }

}
