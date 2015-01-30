package info.batey.eventstore.dao;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import info.batey.eventstore.KafkaConfig;
import info.batey.eventstore.domain.CustomerEvent;
import info.batey.kafka.EmbeddedKafka;
import info.batey.kafka.EmbeddedZookeeper;
import kafka.admin.CreateTopicCommand;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringDecoder;
import kafka.serializer.StringEncoder;
import org.junit.*;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class KafkaEventStoreTest {

    private static EmbeddedZookeeper embeddedZookeeper = new EmbeddedZookeeper(5000);
    private static EmbeddedKafka embeddedKafka = new EmbeddedKafka("localhost:5000", 5001);
    private static String topicName = "Events";

    @BeforeClass
    public static void startKafka() throws Exception {
        embeddedZookeeper.startup();
        embeddedKafka.startup();
        embeddedKafka.createTopic(topicName);
    }

    @AfterClass
    public static void stopKafka() throws Exception {
        embeddedKafka.shutdown();
        embeddedZookeeper.shutdown();
    }

    private KafkaEventStore underTest;

    @Before
    public void setup() throws Exception {
        Properties props = new Properties();
        props.put("serializer.class", StringEncoder.class.getName());
        props.put("metadata.broker.list", "localhost:5001");
        props.put("consumer.id", "test");
        ProducerConfig config = new ProducerConfig(props);
        underTest =  new KafkaEventStore(new Producer<>(config), new KafkaConfig("localhost:5001", topicName));
    }

    @Test
    public void storeCustomerEvent() throws Exception {
        CustomerEvent customerEvent = new CustomerEvent("customerId", "staffId", "WEB", "GOLD_CUSTOMERS", "Fancy content", UUID.randomUUID(), "LOGIN");
        underTest.storeEvent(customerEvent);

        Properties consumerProperties = new Properties();
        consumerProperties.put("zookeeper.connect", "localhost:5000");
        consumerProperties.put("group.id", "1");
        ConsumerConnector javaConsumerConnector = Consumer.createJavaConsumerConnector(new ConsumerConfig(consumerProperties));
        Map<String, List<KafkaStream<byte[], byte[]>>> events = javaConsumerConnector.createMessageStreams(ImmutableMap.of(topicName, 1));
        List<KafkaStream<byte[], byte[]>> events1 = events.get(topicName);

        KafkaStream<byte[], byte[]> messageAndMetadatas = events1.get(0);
        ConsumerIterator<byte[], byte[]> iterator = messageAndMetadatas.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("msg", new String(iterator.next().message()));
    }
}