package info.batey.eventstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerEventController {

    private final static Logger LOGGER = LoggerFactory.getLogger(CustomerEventController.class);

    @Autowired
    private CustomerEventDao customerEventDao;

    @RequestMapping("/events")
    public Iterable<CustomerEvent> getEvents() {
        return customerEventDao.getAllCustomerEvents().all();
    }

    @RequestMapping("/events/{customerId}")
    public Iterable<CustomerEvent> getEventsForTime(@PathVariable String customerId,
                                                @RequestParam(required = false) Long startTime,
                                                @RequestParam(required = false) Long endTime) {
        try {
            if (startTime != null && endTime != null) {
                LOGGER.info("Getting events from {} to {} for customer {}", startTime, endTime, customerId);
                return customerEventDao.getCustomerEventsForTime(customerId, startTime, endTime).all();
            } else {
                LOGGER.info("Getting events all or customer {}", customerId);
                return customerEventDao.getCustomerEvents(customerId).all();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
