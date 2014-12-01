package info.batey.eventstore;

import com.datastax.driver.mapping.EnumType;
import com.datastax.driver.mapping.annotations.*;

import java.util.Map;
import java.util.UUID;

@Table(keyspace = "customers", name = "customer_events_type")
public class CustomerEventType {
    @PartitionKey
    @Column(name = "customer_id")
    private String customerId;

    @ClusteringColumn()
    private UUID time;

    @Column(name = "staff_id")
    private String staffId;

    @Frozen
    private Store store;

    @Column(name = "event_type")
    private String eventType;

    private Map<String, String> tags;

    public CustomerEventType(String customerId, UUID time, String staffId, Store store, String eventType, Map<String, String> tags) {
        this.customerId = customerId;
        this.time = time;
        this.staffId = staffId;
        this.store = store;
        this.eventType = eventType;
        this.tags = tags;
    }

    public CustomerEventType() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public UUID getTime() {
        return time;
    }

    public void setTime(UUID time) {
        this.time = time;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
}
