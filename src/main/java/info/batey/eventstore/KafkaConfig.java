package info.batey.eventstore;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfig {
    @NotNull
    private String brokerlist;

    @NotNull
    private String topic;

    public KafkaConfig(String brokerList, String topic) {
        this.brokerlist = brokerList;
        this.topic = topic;
    }

    public KafkaConfig() {
    }

    public String getBrokerlist() {
        return brokerlist;
    }

    public String getTopic() {
        return topic;
    }

    public void setBrokerlist(String brokerlist) {
        this.brokerlist = brokerlist;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
