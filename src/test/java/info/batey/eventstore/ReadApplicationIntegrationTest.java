package info.batey.eventstore;

import com.datastax.driver.core.Cluster;
import info.batey.eventstore.domain.CustomerEvent;
import org.cassandraunit.CQLDataLoader;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = true, value = {"cassandra.port=9142", "cassandra.keyspace=EventsTest"})
public class ReadApplicationIntegrationTest {

    @Value("${local.server.port}")
    private int port;

    @Rule
    public CassandraCQLUnit cassandraCQLUnit;
    private final CustomerEvent chbateyCustomerEvent = new CustomerEvent(
            "chbatey",
            "trevor",
            "online",
            "NEW_CUSTOMER",
            "awesome content",
            UUID.fromString("c8236b90-a316-11e4-871a-bb9f5ed27228"),
            "BASKET_ADD"
    );
    private final CustomerEvent billyCustomerEvent = new CustomerEvent(
            "billy",
            "trevor",
            "online",
            "NEW_CUSTOMER",
            "awesome content",
            UUID.fromString("c8236b90-a316-11e4-871a-bb9f5ed27228"),
            "BASKET_ADD"
    );

    @BeforeClass
    public static void setup() throws Exception {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
        CQLDataLoader cqlDataLoader = new CQLDataLoader(Cluster.builder().addContactPoint("127.0.0.1").withPort(9142).build().connect());
        cqlDataLoader.load(new ClassPathCQLDataSet("Events.cql", "eventstest"));

    }

    @AfterClass
    public static void tearDown() throws Exception {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @Before
    public void loadData() {
    }

    private RestTemplate template = new TestRestTemplate();

    @Test
    public void getAlEvents() throws Exception {
        ResponseEntity<CustomerEvent[]> allEvents = template.getForEntity("http://localhost:" + port + "/api/event", CustomerEvent[].class);

        assertThat(allEvents.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(allEvents.getBody(), equalTo(new CustomerEvent[] {billyCustomerEvent, chbateyCustomerEvent}));
    }

    @Test
    public void getEventsForSpcificCustomer() throws Exception {
        ResponseEntity<CustomerEvent[]> chbateyEvents = template.getForEntity("http://localhost:" + port + "/api/event/chbatey", CustomerEvent[].class);

        assertThat(chbateyEvents.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(chbateyEvents.getBody(), equalTo(new CustomerEvent[] { chbateyCustomerEvent }));
    }
}