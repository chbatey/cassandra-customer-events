import com.datastax.driver.core.utils.UUIDs;
import info.batey.eventstore.KafkaConfig;
import info.batey.eventstore.dao.KafkaEventStore;
import info.batey.eventstore.domain.CustomerEvent;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import java.util.Properties;

public class KafkaSandbox {

    public static void main(String[] args) throws InterruptedException {

        Properties props = new Properties();
        props.put("serializer.class", StringEncoder.class.getName());
//        props.put("zk.connect", "192.168.86.5:2181");
        props.put("metadata.broker.list", "192.168.86.10:9092");

        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<>(config);

        KafkaConfig kafkaConfig = new KafkaConfig("192.168.86.10:9092", "Events");

        KafkaEventStore kafkaEventStore = new KafkaEventStore(producer, kafkaConfig);

        kafkaEventStore.storeEvent(new CustomerEvent("customer", "staff", "store type", "group", "some awesome content", UUIDs.timeBased(), "BUY"));

        Thread.sleep(2000);

        producer.close();
    }
}
