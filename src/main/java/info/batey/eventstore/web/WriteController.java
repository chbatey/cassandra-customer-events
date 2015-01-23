package info.batey.eventstore.web;

import info.batey.eventstore.dao.EventStore;
import info.batey.eventstore.domain.CustomerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WriteController {
    private final static Logger LOGGER = LoggerFactory.getLogger(WriteController.class);

    @Autowired
    private EventStore eventStore;

    @RequestMapping(value = "/api/event", method = {RequestMethod.POST}, consumes = {"application/json"})
    public void storeEvent(@RequestBody CustomerEvent event) {
        eventStore.storeEvent(event);
    }
}
