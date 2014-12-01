package info.batey.eventstore;

import com.datastax.driver.mapping.annotations.UDT;

@UDT(keyspace = "customers", name = "store")
public class Store {
    private String name;
    private StoreType type;
    private String postcode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StoreType getType() {
        return type;
    }

    public void setType(StoreType type) {
        this.type = type;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @Override
    public String toString() {
        return "Store{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}
