package racegrid.model;


import java.util.UUID;

public class UserAuth {
    private final Id id;
    private final UUID hash;

    public UserAuth(Id id, UUID hash) {
        this.id = id;
        this.hash = hash;
    }

    public Id id() {
        return id;
    }

    public UUID hash() {
        return hash;
    }
}
