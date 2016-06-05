package racegrid.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class Player {
    private final String name;
    private final Id id;

    @JsonCreator
    public Player(@JsonProperty("name") String name,
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
}
