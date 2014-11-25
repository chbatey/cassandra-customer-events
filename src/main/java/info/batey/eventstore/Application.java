package info.batey.eventstore;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableConfigurationProperties(CassandraConfig.class)
public class Application {

    @Autowired
    public CassandraConfig cassandraConfig;


    @Bean
    public Session session() {
        return Cluster.builder()
                .addContactPoint(cassandraConfig.getHost())
                .build()
                .connect();
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
