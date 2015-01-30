package info.batey.kafka;

import com.datastax.driver.core.utils.UUIDs;
import info.batey.eventstore.KafkaConfig;
import info.batey.eventstore.dao.KafkaEventStore;
import info.batey.eventstore.domain.CustomerEvent;
import info.batey.kafka.unit.Kafka;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;

import java.util.List;
import java.util.Properties;

public class ExampleKafka {
    public static void main(String[] args) throws Exception {
        int kafkaPort = 9020;
        int zkPort = 2181;
        String brokerHost = "localhost:9020";
        String topic = "Events";

        Kafka kafka = new Kafka(zkPort, kafkaPort);
        kafka.startup();

        System.out.printf("Kafka started");

        kafka.createTopic("Hello");

        Properties props = new Properties();
        props.put("serializer.class", StringEncoder.class.getName());
        props.put("metadata.broker.list", brokerHost);

        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<>(config);
        KafkaConfig kafkaConfig = new KafkaConfig(brokerHost, topic);

        KafkaEventStore kafkaEventStore = new KafkaEventStore(producer, kafkaConfig);
        kafkaEventStore.storeEvent(new CustomerEvent("customer", "staff", "store type", "group", "some awesome content", UUIDs.timeBased(), "BUY"));
        kafkaEventStore.storeEvent(new CustomerEvent("customer", "staff", "store type", "group", "some awesome content", UUIDs.timeBased(), "BUY"));

        List<String> messages = kafka.readMessages(topic, 2);
        System.out.println("Received " + messages);

        System.out.println("Shutting down kafka");
        kafka.shutdown();

    }
}
