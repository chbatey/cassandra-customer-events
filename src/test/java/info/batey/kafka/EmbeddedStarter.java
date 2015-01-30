package info.batey.kafka;

public class EmbeddedStarter {
    public static void main(String[] args) throws Exception {
        EmbeddedZookeeper ex = new EmbeddedZookeeper(2181);
        ex.startup();
        System.out.printf("Zookeeper started");

        EmbeddedKafka embeddedKafka = new EmbeddedKafka("localhost:2181", 9020);
        embeddedKafka.startup();
        System.out.printf("Kafka started");

        embeddedKafka.createTopic("Hello");

        System.in.read();


        System.out.println("Shutting down kafka");
        embeddedKafka.shutdown();

        System.out.println("Shutting down zookeeper");
        ex.shutdown();
        System.out.println("Zookeeper down");


    }
}
