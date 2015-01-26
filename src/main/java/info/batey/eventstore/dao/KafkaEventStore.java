package info.batey.eventstore.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.batey.eventstore.KafkaConfig;
import info.batey.eventstore.domain.CustomerEvent;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaEventStore implements EventStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaEventStore.class);

    private final Producer<String, String> producer;
    private KafkaConfig kafkaConfig;

    private final ObjectMapper om = new ObjectMapper();

    public KafkaEventStore(Producer<String, String> producer, KafkaConfig kafkaConfig) {
        this.producer = producer;
        this.kafkaConfig = kafkaConfig;
    }

    @Override
    public void storeEvent(CustomerEvent event) {
        String serialisedEvent;
        try {
            serialisedEvent = om.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to send event");
        }
        LOGGER.debug("Sending event {} to topic {}", serialisedEvent,kafkaConfig.getTopic());
        producer.send(new KeyedMessage<>(kafkaConfig.getTopic(), event.getCustomerId(), serialisedEvent));
    }
}
