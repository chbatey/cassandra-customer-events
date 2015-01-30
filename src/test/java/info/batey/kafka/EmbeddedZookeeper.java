package info.batey.kafka;
import com.google.common.io.Files;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;

public class EmbeddedZookeeper {
    private int port;

    private ServerCnxnFactory factory;
    private File snapshotDir;
    private File logDir;

    public EmbeddedZookeeper(int port) {
        this.port = port;
    }

    public void startup() throws IOException{

        this.snapshotDir = Files.createTempDir();
        this.logDir = Files.createTempDir();
        this.snapshotDir.deleteOnExit();
        this.logDir.delete();

        try {
            int tickTime = 500;
            ZooKeeperServer zkServer = new ZooKeeperServer(snapshotDir, logDir, tickTime);
            this.factory = NIOServerCnxnFactory.createFactory();
            this.factory.configure(new InetSocketAddress("localhost", port), 16);
            factory.startup(zkServer);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }


    public void shutdown() {
        factory.shutdown();
    }
}