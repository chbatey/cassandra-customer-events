package info.batey.eventstore;

import info.batey.eventstore.domain.CustomerEvent;
import org.cassandraunit.CassandraCQLUnit;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.spring.CassandraDataSet;
import org.cassandraunit.spring.CassandraUnit;
import org.cassandraunit.spring.CassandraUnitTestExecutionListener;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
@WebIntegrationTest(randomPort = false, value = {"cassandra.port=9142", "cassandra.keyspace=EventsTest"})
//@TestExecutionListeners({CassandraUnitTestExecutionListener.class})
//@CassandraUnit
//@CassandraDataSet(keyspace = "EventsTest", value = "Events.cql")
public class ReadApplicationTest {

//    @Value("${local.server.port}")
    private int port = 8080;

    @BeforeClass
    public static void setup() throws Exception {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra();
    }


    private RestTemplate template = new TestRestTemplate();

    @Test
    public void testSomething() throws Exception {
        CustomerEvent expectedCustomerEvent = new CustomerEvent(
                "chbatey",
                "trevor",
                "online",
                "NEW_CUSTOMER",
                "some awesome content",
                UUID.fromString("c8236b90-a316-11e4-871a-bb9f5ed27228"),
                "BASKET_ADD"
        );
        ResponseEntity<CustomerEvent[]> forEntity = template.getForEntity("http://localhost:" + port + "/api/event", CustomerEvent[].class);

        assertThat(forEntity.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(forEntity.getBody(), equalTo(new CustomerEvent[] {expectedCustomerEvent  }));
    }
}