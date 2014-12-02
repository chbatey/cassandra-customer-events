package info.batey.eventstore;

import com.datastax.driver.core.Cluster;
import rx.Observable;

public class Sandbox {
    public static void main(String[] args) throws InterruptedException {

        Cluster cluster = Cluster.builder().addContactPoint("localhost").build();

        CustomerEventDaoAsync customerEventDao = new CustomerEventDaoAsync(cluster.connect("customers"));
        customerEventDao.prepareStatements();

        Observable<CustomerEvent> chbatey = customerEventDao.getCustomerEventsObservable("chbatey");

        chbatey.subscribe(System.out::println);

        Thread.sleep(10000);

        cluster.close();
    }
}
