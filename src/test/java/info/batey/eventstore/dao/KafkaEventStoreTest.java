package info.batey.eventstore.dao;

import com.google.common.collect.Lists;
import info.batey.eventstore.KafkaConfig;
import info.batey.eventstore.domain.CustomerEvent;
import info.batey.kafka.unit.Kafka;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringEncoder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class KafkaEventStoreTest {

    private static Kafka kafka = new Kafka(5000, 5001);
    private static String topicName = "Events";

    @BeforeClass
    public static void startKafka() throws Exception {
        kafka.startup();
        kafka.createTopic(topicName);
    }

    @AfterClass
    public static void stopKafka() throws Exception {
        kafka.shutdown();
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
        //given
        CustomerEvent customerEvent = new CustomerEvent("customerId", "staffId", "WEB", "GOLD_CUSTOMERS", "Fancy content", UUID.fromString("1368baec-0565-49da-90b7-4ab07a7d375d"), "LOGIN");

        //when
        underTest.storeEvent(customerEvent);

        //then
        List<String> messages = kafka.readMessages(topicName, 1);
        assertEquals(Lists.newArrayList("{\"group\":\"GOLD_CUSTOMERS\",\"content\":\"Fancy content\",\"time\":\"1368baec-0565-49da-90b7-4ab07a7d375d\",\"customer_id\":\"customerId\",\"staff_id\":\"staffId\",\"store_type\":\"WEB\",\"event_type\":\"LOGIN\"}"),
                messages);
    }

    @Test
    public void storingMultipleEvents() throws Exception {
        //given
        CustomerEvent customerEvent = new CustomerEvent("customerId", "staffId", "WEB", "GOLD_CUSTOMERS", "Fancy content", UUID.fromString("1368baec-0565-49da-90b7-4ab07a7d375d"), "LOGIN");
        CustomerEvent customerEvent2 = new CustomerEvent("event2", "staffId", "WEB", "GOLD_CUSTOMERS", "Fancy content", UUID.fromString("1368baec-0565-49da-90b7-4ab07a7d375d"), "LOGIN");

        //when
        underTest.storeEvents(customerEvent, customerEvent2);

        //then
        List<String> messages = kafka.readMessages(topicName, 2);
        assertEquals(Lists.newArrayList(
                        "{\"group\":\"GOLD_CUSTOMERS\",\"content\":\"Fancy content\",\"time\":\"1368baec-0565-49da-90b7-4ab07a7d375d\",\"customer_id\":\"customerId\",\"staff_id\":\"staffId\",\"store_type\":\"WEB\",\"event_type\":\"LOGIN\"}",
                        "{\"group\":\"GOLD_CUSTOMERS\",\"content\":\"Fancy content\",\"time\":\"1368baec-0565-49da-90b7-4ab07a7d375d\",\"customer_id\":\"event2\",\"staff_id\":\"staffId\",\"store_type\":\"WEB\",\"event_type\":\"LOGIN\"}"),
                messages);
    }
}