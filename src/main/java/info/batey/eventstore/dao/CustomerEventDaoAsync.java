package info.batey.eventstore.dao;

import com.datastax.driver.core.*;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.utils.UUIDs;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import info.batey.eventstore.domain.CustomerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.schedulers.Schedulers;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

@Component
public class CustomerEventDaoAsync {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerEventDaoAsync.class);

    private Session session;

    private PreparedStatement getEventsForCustomer;

    @Autowired
    public CustomerEventDaoAsync(Session session) {
        this.session = session;
    }

    @PostConstruct
    public void prepareStatements() {
        getEventsForCustomer = session.prepare("select * from customer_events where customer_id = ?");
    }


    /*
     * Async gets all the events for a single customer + transform
     */
    public ListenableFuture<List<CustomerEvent>> getCustomerEventsAsync(String customerId) {
        BoundStatement boundStatement = getEventsForCustomer.bind(customerId);
        ListenableFuture<ResultSet> resultSetFuture = session.executeAsync(boundStatement);
        ListenableFuture<List<CustomerEvent>> transform = Futures.transform(resultSetFuture, (com.google.common.base.Function<ResultSet, List<CustomerEvent>>)
                queryResult -> queryResult.all().stream().map(mapCustomerEvent()).collect(Collectors.toList()));
        return transform;
    }

    /*
     * Async gets all the events for a single customer + transform
     */
    public Observable<CustomerEvent> getCustomerEventsObservable(String customerId) {
        BoundStatement boundStatement = getEventsForCustomer.bind(customerId);
        ListenableFuture<ResultSet> resultSetFuture = session.executeAsync(boundStatement);
        Observable<ResultSet> observable = Observable.from(resultSetFuture, Schedulers.io());
        Observable<Row> rowObservable = observable.flatMapIterable(result -> result);
        return rowObservable.map(row -> new CustomerEvent(
                row.getString("customer_id"),
                row.getString("staff_id"),
                row.getString("store_type"),
                row.getString("group"),
                row.getString("content"),
                row.getUUID("time"),
                row.getString("event_type")
        ));

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
