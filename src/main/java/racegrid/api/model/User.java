package racegrid.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private final String name;
    private final Id id;

    @JsonCreator
    public User(@JsonProperty("name") String name,
                @JsonProperty("id") Id id) {
        this.name = name;
        this.id = id;
    }

    @JsonProperty
    public String name() {
        return name;
    }

    @JsonProperty
    public Id id() {
        return id;
    }

    public String toString() {
        return "User(" + name + ")";
    }
}
