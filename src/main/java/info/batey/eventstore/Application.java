
package info.batey.eventstore;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import info.batey.eventstore.dao.EventStore;
import info.batey.eventstore.dao.KafkaEventStore;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableConfigurationProperties({CassandraConfig.class, KafkaConfig.class})
public class Application {

    @Autowired
    private CassandraConfig cassandraConfig;

    @Autowired
    private KafkaConfig kafkaConfig;

    @Bean
    public Session session() {
        return Cluster.builder()
                .addContactPoint(cassandraConfig.getHost())
                .build()
                .connect(cassandraConfig.getKeyspace());
    }

    @Bean
    public EventStore eventStore() {
        Properties props = new Properties();
        props.put("serializer.class", StringEncoder.class.getName());
        props.put("metadata.broker.list", kafkaConfig.getBrokerlist());
        ProducerConfig config = new ProducerConfig(props);
        return new KafkaEventStore(new Producer<>(config), kafkaConfig);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
