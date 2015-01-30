package info.batey.kafka;

import com.google.common.io.Files;
import kafka.admin.AdminUtils;
import kafka.admin.CreateTopicCommand;
import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;
import kafka.utils.ZKStringSerializer;
import kafka.utils.ZKStringSerializer$;
import org.I0Itec.zkclient.ZkClient;

import java.io.File;
import java.util.Properties;

public class EmbeddedKafka {
    private final String zkConnection;
    private final Properties baseProperties;
    private int port;

    private final String brokerString;

    private KafkaServerStartable broker;

    public EmbeddedKafka(String zkConnection, int port) {
        this(zkConnection, new Properties(), port);
    }

    public EmbeddedKafka(String zkConnection, Properties baseProperties, int port) {
        this.zkConnection = zkConnection;
        this.baseProperties = baseProperties;
        this.port = port;
        this.brokerString = "localhost:" + port;
    }

    public void startup() {

        File logDir = Files.createTempDir();
        logDir.deleteOnExit();

        Properties properties = new Properties();
        properties.putAll(baseProperties);
        properties.setProperty("zookeeper.connect", zkConnection);
        properties.setProperty("broker.id", "1");
        properties.setProperty("host.name", "localhost");
        properties.setProperty("port", Integer.toString(port));
        properties.setProperty("log.dir", logDir.getAbsolutePath());
        properties.setProperty("log.flush.interval.messages", String.valueOf(1));

        broker = new KafkaServerStartable(new KafkaConfig(properties));
        broker.startup();
    }

    public void createTopic(String topicName) {
        String [] arguments = new String[8];
        arguments[0] = "--zookeeper";
        arguments[1] = zkConnection;
        arguments[2] = "--replica";
        arguments[3] = "1";
        arguments[4] = "--partition";
        arguments[5] = "1";
        arguments[6] = "--topic";
        arguments[7] = topicName;
        CreateTopicCommand.main(arguments);
    }


    public void shutdown() {
        if (broker != null) broker.shutdown();
    }

}

