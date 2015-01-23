package info.batey.eventstore.dao;

import info.batey.eventstore.domain.CustomerEvent;

public interface EventStore {
    void storeEvent(CustomerEvent event);
}
