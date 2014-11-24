package info.batey.eventstore;

import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Table(keyspace = "customers", name = "customer_events")
public class CustomerEvent {
    @PartitionKey
    private String customerId;

    @ClusteringColumn()
    private UUID time;

    private String staffId;

    private String storeType;

    private String eventType;

    private Map<String, String> tags;

    public CustomerEvent(String customerId, UUID time, String staffId, String storeType, String eventType, Map<String, String> tags) {
        this.customerId = customerId;
        this.time = time;
        this.staffId = staffId;
        this.storeType = storeType;
        this.eventType = eventType;
        this.tags = tags;
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

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
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
