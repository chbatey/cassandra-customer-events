package info.batey.eventstore;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration()
@EnableAutoConfiguration
@ComponentScan
@EnableConfigurationProperties(CassandraConfig.class)
public class Application {

    @Autowired
    private CassandraConfig cassandraConfig;

    @Autowired
    private Session session;


    @Bean
    public Session session() {
        return Cluster.builder()
                .addContactPoint(cassandraConfig.getHost())
                .build().connect();
    }

    @Bean
    public CustomerEventDao customerEventDao() {
        MappingManager mappingManager = new MappingManager(session);
        return mappingManager.createAccessor(CustomerEventDao.class);
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
