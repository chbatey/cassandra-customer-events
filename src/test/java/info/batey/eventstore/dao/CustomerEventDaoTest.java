package info.batey.eventstore.dao;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.utils.UUIDs;
import info.batey.eventstore.domain.CustomerEvent;
import org.junit.*;
import org.scassandra.http.client.ActivityClient;
import org.scassandra.http.client.PrimingClient;
import org.scassandra.http.client.PrimingRequest;
import org.scassandra.junit.ScassandraServerRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.scassandra.http.client.types.ColumnMetadata.*;
import static org.scassandra.cql.PrimitiveType.*;
import static org.scassandra.cql.MapType.*;
import static org.scassandra.cql.SetType.*;
import static org.scassandra.cql.ListType.*;

public class CustomerEventDaoTest {

    @ClassRule
    public static ScassandraServerRule scassandra = new ScassandraServerRule();

    @Rule
    public ScassandraServerRule reset = scassandra;

    private PrimingClient primingClient = scassandra.primingClient();
    private ActivityClient activityClient = scassandra.activityClient();

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
    public void getAllCustomerEvents() throws Exception {
        //given
        CustomerEvent primedCustomerEvent = new CustomerEvent("chbatey", "charlie", "Web", "NEW_CUSTOMERS", "Content", UUIDs.timeBased(), "ADD_TO_BASKET");
        Map<String, Object> mockedRow = new HashMap<>();
        mockedRow.put("customer_id", primedCustomerEvent.getCustomerId());
        mockedRow.put("staff_id", primedCustomerEvent.getStaffId());
        mockedRow.put("store_type", primedCustomerEvent.getStoreType());
        mockedRow.put("group", primedCustomerEvent.getGroup());
        mockedRow.put("content", primedCustomerEvent.getContent());
        mockedRow.put("time", primedCustomerEvent.getTime());
        mockedRow.put("event_type", primedCustomerEvent.getEventType());

        PrimingRequest prime = PrimingRequest.queryBuilder()
                .withQueryPattern(".*customer_events.*")
                .withRows(mockedRow)
                .withColumnTypes(column("time", TIMEUUID))
                .build();
        primingClient.prime(prime);

        //when
        List<CustomerEvent> allCustomerEvents = underTest.getAllCustomerEvents();

        //then
        assertThat("Expected a single customer event", allCustomerEvents.size(), equalTo(1));
        assertThat(primedCustomerEvent, equalTo(allCustomerEvents.get(0)));
    }
}