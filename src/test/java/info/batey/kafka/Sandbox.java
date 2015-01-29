package info.batey.kafka;

public class Sandbox {
    public static void main(String[] args) throws Exception {
        EmbeddedZookeeper ex = new EmbeddedZookeeper();
        ex.startup();
    }
}
