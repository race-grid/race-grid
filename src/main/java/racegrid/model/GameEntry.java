package racegrid.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.ArrayList;
import java.util.List;

public class GameEntry {

    private final Id id;
    private final List<User> users;

    @JsonCreator
    public GameEntry(@JsonProperty("id") Id id,
                     @JsonProperty("users") List<User> users) {
        this.id = id;
        this.users = users;
    }

    @JsonProperty
    public Id id() {
        return id;
    }

    @JsonProperty
    public List<User> users() {
        return new ArrayList<>(users);
    }

    public String toString() {
        return "Game(" + id + ")";
    }
}
