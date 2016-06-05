package racegrid.model;

import java.util.UUID;

public class NewUserResponse {
    private User user;
    private UUID userHash;
    private boolean createdNewUser;

    public static NewUserResponse success(User newUser, UUID playerHash) {
        NewUserResponse response = new NewUserResponse();
        response.user = newUser;
        response.userHash = playerHash;
        response.createdNewUser = true;
        return response;
    }

    public static NewUserResponse fail() {
        return new NewUserResponse();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isCreatedNewUser() {
        return createdNewUser;
    }

    public void setCreatedNewUser(boolean createdNewUser) {
        this.createdNewUser = createdNewUser;
    }

    public UUID getUserHash() {
        return userHash;
    }

    public void setUserHash(UUID userHash) {
        this.userHash = userHash;
    }
}
