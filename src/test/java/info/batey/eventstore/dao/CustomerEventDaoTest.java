package info.batey.eventstore.dao;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.utils.UUIDs;
import info.batey.eventstore.domain.CustomerEvent;
import org.junit.*;
import org.scassandra.http.client.ActivityClient;
import org.scassandra.http.client.PreparedStatementExecution;
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
import static org.scassandra.matchers.Matchers.*;

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
    }

    // Uses a regular query
    @Test
    public void getAllCustomerEvents() throws Exception {
        //given
        CustomerEvent primedCustomerEvent = new CustomerEvent("chbatey", "charlie", "Web", "NEW_CUSTOMERS", "Content", UUIDs.timeBased(), "ADD_TO_BASKET");
        Map<String, Object> mockedRow = buildRowForCustomerEvent(primedCustomerEvent);

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

    // uses a prepared statement
    @Test
    public void getEventsForASingleCustomer() throws Exception {
        //given
        underTest.prepareStatements();
        CustomerEvent primedEventForChbatey = new CustomerEvent("chbatey", "charlie", "Web", "NEW_CUSTOMERS", "Content", UUIDs.timeBased(), "ADD_TO_BASKET");
        Map<String, Object> chbateyRowValues = buildRowForCustomerEvent(primedEventForChbatey);

        PrimingRequest prime = PrimingRequest.preparedStatementBuilder()
                .withQuery("select * from customer_events where customer_id = ?")
                .withRows(chbateyRowValues)
                .withVariableTypes(TEXT)
                .withColumnTypes(column("time", TIMEUUID))
                .build();
        primingClient.prime(prime);

        //when
        List<CustomerEvent> allCustomerEvents = underTest.getCustomerEvents("chbatey");

        //then
        assertThat("Expected a single customer event", allCustomerEvents.size(), equalTo(1));
        assertThat(primedEventForChbatey, equalTo(allCustomerEvents.get(0)));

        PreparedStatementExecution expectedExecution = PreparedStatementExecution.builder()
                .withConsistency("ONE")
                .withPreparedStatementText("select * from customer_events where customer_id = ?")
                .withVariables("chbatey")
                .build();
        assertThat(activityClient.retrievePreparedStatementExecutions(), preparedStatementRecorded(expectedExecution));
    }

    @Test
    public void getEventsForTimeSlice() throws Exception {
        //given
        CustomerEvent primedEventForChbatey = new CustomerEvent("chbatey", "charlie", "Web", "NEW_CUSTOMERS", "Content", UUIDs.timeBased(), "ADD_TO_BASKET");
        Map<String, Object> chbateyRowValues = buildRowForCustomerEvent(primedEventForChbatey);

        PrimingRequest prime = PrimingRequest.preparedStatementBuilder()
                .withQuery("select * from customer_events where customer_id = ? and time > ? and time < ?")
                .withRows(chbateyRowValues)
                .withVariableTypes(TEXT, TIMEUUID, TIMEUUID)
                .withColumnTypes(column("time", TIMEUUID))
                .build();
        primingClient.prime(prime);
        long startTime = 1;
        long endTime = 2;
        underTest.prepareStatements();

        //when
        List<CustomerEvent> allCustomerEvents = underTest.getCustomerEventsForTime("chbatey", startTime, endTime);

        //then
        assertThat("Expected a single customer event", allCustomerEvents.size(), equalTo(1));
        assertThat(primedEventForChbatey, equalTo(allCustomerEvents.get(0)));

        PreparedStatementExecution expectedExecution = PreparedStatementExecution.builder()
                .withConsistency("ONE")
                .withPreparedStatementText("select * from customer_events where customer_id = ? and time > ? and time < ?")
                .withVariables("chbatey", UUIDs.startOf(startTime), UUIDs.endOf(endTime))
                .build();
        assertThat(activityClient.retrievePreparedStatementExecutions(), preparedStatementRecorded(expectedExecution));
    }

    private Map<String, Object> buildRowForCustomerEvent(CustomerEvent primedEventForChbatey) {
        Map<String, Object> chbateyRowValues = new HashMap<>();
        chbateyRowValues.put("customer_id", primedEventForChbatey.getCustomerId());
        chbateyRowValues.put("staff_id", primedEventForChbatey.getStaffId());
        chbateyRowValues.put("store_type", primedEventForChbatey.getStoreType());
        chbateyRowValues.put("group", primedEventForChbatey.getGroup());
        chbateyRowValues.put("content", primedEventForChbatey.getContent());
        chbateyRowValues.put("time", primedEventForChbatey.getTime());
        chbateyRowValues.put("event_type", primedEventForChbatey.getEventType());
        return chbateyRowValues;
    }
}