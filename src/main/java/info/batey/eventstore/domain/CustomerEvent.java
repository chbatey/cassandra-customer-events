package info.batey.eventstore.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    @Override
    public String toString() {
        return "CustomerEvent{" +
                "customerId='" + customerId + '\'' +
                ", staffId='" + staffId + '\'' +
                ", storeType='" + storeType + '\'' +
                ", group='" + group + '\'' +
                ", content='" + content + '\'' +
                ", time=" + time +
                ", eventType='" + eventType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerEvent that = (CustomerEvent) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;
        if (customerId != null ? !customerId.equals(that.customerId) : that.customerId != null) return false;
        if (eventType != null ? !eventType.equals(that.eventType) : that.eventType != null) return false;
        if (group != null ? !group.equals(that.group) : that.group != null) return false;
        if (staffId != null ? !staffId.equals(that.staffId) : that.staffId != null) return false;
        if (storeType != null ? !storeType.equals(that.storeType) : that.storeType != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = customerId != null ? customerId.hashCode() : 0;
        result = 31 * result + (staffId != null ? staffId.hashCode() : 0);
        result = 31 * result + (storeType != null ? storeType.hashCode() : 0);
        result = 31 * result + (group != null ? group.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (eventType != null ? eventType.hashCode() : 0);
        return result;
    }
}
