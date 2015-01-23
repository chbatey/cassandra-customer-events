package info.batey.eventstore;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties("cassandra")
public class CassandraConfig {

    @NotNull
    private String host;

    @NotNull
    private String keyspace;

    public String getHost() {
        return host;
    }

    public String getKeyspace() {
        return keyspace;
    }


    public void setHost(String host) {
        this.host = host;
    }

    public void setKeyspace(String keyspace) {
        this.keyspace = keyspace;
    }
}
