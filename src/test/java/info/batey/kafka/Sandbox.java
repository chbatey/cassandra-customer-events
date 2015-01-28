package info.batey.kafka;

public class Sandbox {
    public static void main(String[] args) throws Exception {

        EmbeddedZookeeper ex = new EmbeddedZookeeper(12000);
        ex.startup();
    }
}
