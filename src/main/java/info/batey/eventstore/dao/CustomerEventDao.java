package info.batey.eventstore.dao;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import info.batey.eventstore.domain.CustomerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.gt;
import static com.datastax.driver.core.querybuilder.QueryBuilder.lt;

@Component
public class CustomerEventDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerEventDao.class);

    private Session session;

    private PreparedStatement getEventsForCustomer;

    @Autowired
    public CustomerEventDao(Session session) {
        this.session = session;
    }

    @PostConstruct
    public void prepareStatements() {
        getEventsForCustomer = session.prepare("select * from customer_events where customer_id = ?");
    }

    public List<CustomerEvent> getCustomerEvents(String customerId) {
        BoundStatement boundStatement = getEventsForCustomer.bind(customerId);
        return session.execute(boundStatement).all().stream()
                .map(mapCustomerEvent())
                .collect(Collectors.toList());
    }

    public List<CustomerEvent> getAllCustomerEvents() {
        return session.execute("select * from customer_events")
                .all().stream()
                .map(mapCustomerEvent())
                .collect(Collectors.toList());

    }

    public List<CustomerEvent> getCustomerEventsForTime(String customerId, long startTime, long endTime) {
        Select.Where getCustomers = QueryBuilder.select()
                .all()
                .from("customer_events")
                .where(eq("customer_id", customerId))
                .and(gt("time", UUIDs.startOf(startTime)))
                .and(lt("time", UUIDs.endOf(endTime)));

        LOGGER.info("Executing {}", getCustomers);

        return session.execute(getCustomers).all().stream()
                .map(mapCustomerEvent())
                .collect(Collectors.toList());
    }

    private Function<Row, CustomerEvent> mapCustomerEvent() {
        return row -> new CustomerEvent(
                row.getString("customer_id"),
                row.getString("staff_id"),
                row.getString("store_type"),
                row.getString("group"),
                row.getString("content"),
                row.getUUID("time"),
                row.getString("event_type")
                );
    }
}
