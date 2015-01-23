package info.batey.eventstore.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class CustomerEvent {

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("staff_id")
    private String staffId;

    @JsonProperty("store_type")
    private String storeType;

    @JsonProperty()
    private String group;

    @JsonProperty
    private String content;

    @JsonProperty()
    private UUID time;

    @JsonProperty("event_type")
    private String eventType;

    public CustomerEvent(String customerId, String staffId, String storeType, String group, String content, UUID time, String eventType) {
        this.customerId = customerId;
        this.staffId = staffId;
        this.storeType = storeType;
        this.group = group;
        this.content = content;
        this.time = time;
        this.eventType = eventType;
    }

    public CustomerEvent() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getStoreType() {
        return storeType;
    }

    public String getGroup() {
        return group;
    }

    public String getContent() {
        return content;
    }

    public UUID getTime() {
        return time;
    }

    public String getEventType() {
        return eventType;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(UUID time) {
        this.time = time;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
