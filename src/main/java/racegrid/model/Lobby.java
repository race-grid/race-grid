package racegrid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import racegrid.api.model.Id;
import racegrid.api.model.RacegridError;
import racegrid.api.model.RacegridException;
import racegrid.api.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Lobby {

    private final Id id;
    private User host;
    private final List<User> pendingInvites = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    public Lobby(Id lobbyId, User host) {
        this.id = lobbyId;
        this.host = host;
        users.add(host);
    }

    public Id getId() {
        return id;
    }

    public User getHost() {
        return host;
    }

    public List<User> getPendingInvites() {
        return new ArrayList<>(pendingInvites);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return users.isEmpty();
    }

    public void removePendingInvite(Id invitedId) {
        assertPendingInvite(invitedId);
        pendingInvites.removeIf(u -> u.id().equals(invitedId));
    }

    public void acceptInvite(User invited) {
        removePendingInvite(invited.id());
        addUser(invited);
    }

    private void addUser(User user) {
        assertNoUser(user.id());
        users.add(user);
    }

    public void addPendingInvite(User invited) {
        assertNoPendingInvite(invited.id());
        assertNoUser(invited.id());
        pendingInvites.add(invited);
    }

    public boolean hasPendingInvite(Id invitedId) {
        return pendingInvites.stream()
                .anyMatch(u -> u.id().equals(invitedId));
    }

    public boolean hasUser(Id userId) {
        return users.stream()
                .anyMatch(u -> u.id().equals(userId));
    }

    private void assertNoPendingInvite(Id userId) {
        if (hasPendingInvite(userId)) {
            throw new RacegridException(RacegridError.INTERNAL, "That user is aleady invited: " + userId);
        }
    }

    private void assertPendingInvite(Id invitedId) {
        if (!hasPendingInvite(invitedId)) {
            throw new RacegridException(RacegridError.INTERNAL, "That user is not invited: " + invitedId);
        }
    }

    private void assertNoUser(Id userId) {
        if (hasUser(userId)) {
            throw new RacegridException(RacegridError.INTERNAL, "User already in lobby");
        }
    }

    public void removeUser(Id userId) {
        assertUser(userId);
        users.removeIf(u -> u.id().equals(userId));
        boolean removedWasHost = host.id().equals(userId);
        if (removedWasHost) {
            Optional<User> otherUser = users.stream().findAny();
            otherUser.ifPresent(other -> this.host = other);
        }
    }

    private void assertUser(Id userId) {
        if (!hasUser(userId)) {
            throw new RacegridException(RacegridError.USER_NOT_FOUND, "No such user in lobby: " + userId);
        }
    }
}
