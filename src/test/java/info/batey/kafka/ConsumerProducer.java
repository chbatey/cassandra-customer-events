package info.batey.kafka;

import com.datastax.driver.core.utils.UUIDs;
import com.google.common.collect.ImmutableMap;
import info.batey.eventstore.KafkaConfig;
import info.batey.eventstore.dao.KafkaEventStore;
import info.batey.eventstore.domain.CustomerEvent;
import kafka.consumer.*;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndMetadata;
import kafka.producer.ProducerConfig;
import kafka.serializer.StringDecoder;
import kafka.serializer.StringEncoder;
import kafka.utils.VerifiableProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by chbatey on 30/01/15.
 */
public class ConsumerProducer {
    public static void main(String[] args) throws Exception {
        sendMessage();
        sendMessage();

        List<String> strings = readMessages(2);
        System.out.println("Got messages: " + strings);
//
//
//        SimpleConsumer consumer = new SimpleConsumer("localhost", 9020, 500, 64 * 1024, "test-consumer");
//        consumer.

    }

    private static List<String> readMessages(int expectedMessages) {
        ExecutorService singleThread = Executors.newSingleThreadExecutor();
        String topicName = "Hello";
        Properties consumerProperties = new Properties();
        consumerProperties.put("zookeeper.connect", "localhost:2181");
        consumerProperties.put("group.id", "10");
        consumerProperties.put("socket.timeout.ms", "500");
        consumerProperties.put("consumer.id", "test");
        consumerProperties.put("auto.offset.reset", "smallest");
        ConsumerConnector javaConsumerConnector = Consumer.createJavaConsumerConnector(new ConsumerConfig(consumerProperties));
        StringDecoder stringDecoder = new StringDecoder(new VerifiableProperties(new Properties()));
        Map<String, List<KafkaStream<String, String>>> events = javaConsumerConnector.createMessageStreams(ImmutableMap.of(topicName, 1), stringDecoder, stringDecoder);
        List<KafkaStream<String, String>> events1 = events.get(topicName);
        System.out.println("Got event1 ");
        final KafkaStream<String, String> kafkaStreams = events1.get(0);
        System.out.println("Got stream ");
        System.out.println("Got iterator ");

        Future<List<String>> submit = singleThread.submit(() -> {
            List<String> messages = new ArrayList<>();
            ConsumerIterator<String, String> iterator = kafkaStreams.iterator();
            while (messages.size() != expectedMessages && iterator.hasNext()) {
                System.out.printf("Got message");
                String message = iterator.next().message();
                System.out.println(message);
                messages.add(message);
            }
            return messages;
        });

        try {
            return submit.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException("Didn't get enough messages in time");
        } finally {
            singleThread.shutdown();
            javaConsumerConnector.shutdown();
        }
    }

    private static void sendMessage() throws InterruptedException {
        Properties props = new Properties();
        props.put("serializer.class", StringEncoder.class.getName());
//        props.put("zk.connect", "192.168.86.5:2181");
        props.put("metadata.broker.list", "localhost:9020");

        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<>(config);

        KafkaConfig kafkaConfig = new KafkaConfig("localhost:9020", "Hello");

        KafkaEventStore kafkaEventStore = new KafkaEventStore(producer, kafkaConfig);

        kafkaEventStore.storeEvent(new CustomerEvent("customer", "staff", "store type", "group", "some awesome content", UUIDs.timeBased(), "BUY"));

        Thread.sleep(2000);

        producer.close();
    }
}
