package info.batey.eventstore;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface CustomerEventDao {
    @Query("select * from customers.customer_events where customer_id = :customerId")
    Result<CustomerEvent> getCustomerEvents(String customerId);

    @Query("select * from customers.customer_events")
    Result<CustomerEvent> getAllCustomerEvents();

    @Query("select * from customers.customer_events where customer_id = :customerId and time > minTimeuuid(:startTime) and time < maxTimeuuid(:endTime)")
    Result<CustomerEvent> getCustomerEventsForTime(String customerId, long startTime, long endTime);
}
