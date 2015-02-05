package info.batey.eventstore;

import com.datastax.driver.core.Cluster;
import com.google.common.collect.Lists;
import info.batey.kafka.unit.KafkaUnit;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true, value = {"cassandra.port=9142", "cassandra.keyspace=EventsTest", "kafka.brokerlist=localhost:"+ WriteApplicationIntegrationTest.BROKER_PORT})
public class WriteApplicationIntegrationTest {

    static final int BROKER_PORT = 5003;

    @Value("${local.server.port}")
    private int port;

    @Rule
    public CassandraCQLUnit cassandraCQLUnit;

    private static KafkaUnit kafka;

    @BeforeClass
    public static void setup() throws Exception {
        kafka = new KafkaUnit(5002, BROKER_PORT);
        kafka.startup();
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        CQLDataLoader cqlDataLoader = new CQLDataLoader(Cluster.builder().addContactPoint("127.0.0.1").withPort(9142).build().connect());
        cqlDataLoader.load(new ClassPathCQLDataSet("Events.cql", "eventstest"));
    }

    @AfterClass
    public static void tearDown() throws Exception {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
        kafka.shutdown();
    }

    private RestTemplate template = new TestRestTemplate();

    @Test
    public void writeEvent() throws Exception {
        String event = "{\"group\":\"GOLD_CUSTOMERS\",\"content\":\"Fancy content\",\"time\":\"1368baec-0565-49da-90b7-4ab07a7d375d\",\"customer_id\":\"customerId\",\"staff_id\":\"staffId\",\"store_type\":\"WEB\",\"event_type\":\"LOGIN\"}";
        RequestEntity<String> body = RequestEntity.post(URI.create("http://localhost:" + port + "/api/event")).contentType(MediaType.APPLICATION_JSON).body(event);
        ResponseEntity<Void> allEvents = template.exchange(body, Void.class);

        assertThat(allEvents.getStatusCode(), equalTo(HttpStatus.OK));
        List<String> messages = kafka.readMessages("Events", 1);
        assertThat(messages, equalTo(Lists.newArrayList(event)));
    }


}
