package info.batey.eventstore.dao;

import com.datastax.driver.core.Cluster;
import info.batey.eventstore.domain.CustomerEvent;
import org.junit.*;
import org.scassandra.junit.ScassandraServerRule;

import java.util.List;

@Ignore
public class CustomerEventDaoTest {

    @ClassRule
    public static ScassandraServerRule scassandra = new ScassandraServerRule();

    @Rule
    public ScassandraServerRule reset = scassandra;

    private CustomerEventDao underTest;

    @Before
    public void setUp() throws Exception {
        Cluster cluster = Cluster.builder()
                .addContactPoint("localhost")
                .withPort(8042).build();
        underTest = new CustomerEventDao(cluster.connect());
        underTest.prepareStatements();
    }

    @Test
    public void something() throws Exception {
        List<CustomerEvent> allCustomerEvents = underTest.getAllCustomerEvents();
    }
}