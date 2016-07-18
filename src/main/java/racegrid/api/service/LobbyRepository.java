package racegrid.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import racegrid.api.model.Id;
import racegrid.api.model.RacegridError;
import racegrid.api.model.RacegridException;
import racegrid.api.model.User;
import racegrid.model.Lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class LobbyRepository {

    private final UserRepository userRepository;
    private List<Lobby> lobbies = new ArrayList<>();

    @Autowired
    public LobbyRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Id createLobby(Id hostId) {
        User host = assertUserExists(hostId);
        Id lobbyId = Id.generateUnique();
        Lobby newLobby = new Lobby(lobbyId, host);
        lobbies.add(newLobby);
        return lobbyId;
    }

    public void inviteToLobby(Id userId, Id invitedId) {
        assertUserExists(userId);
        User invited = assertUserExists(invitedId);
        Lobby lobby = lobbyHostedBy(userId);
        lobby.addPendingInvite(invited);
    }

    private Lobby lobbyHostedBy(Id hostId) {
        return lobbies.stream()
                .filter(l -> l.getHost().id().equals(hostId))
                .findAny()
                .orElseThrow(() -> new RacegridException(RacegridError.LOBBY_NOT_FOUND, "User is not host of a lobby"));
    }

    private User assertUserExists(Id userId) {
        Optional<User> user = userRepository.userById(userId);
        return user.orElseThrow(() ->
                new RacegridException(RacegridError.USER_NOT_FOUND, "That user doesn't exist: " + userId)
        );
    }

    public Stream<Lobby> getLobbies() {
        return lobbies.stream();
    }

    public void acceptInvite(Id invitedId) {
        User invited = assertUserExists(invitedId);
        Lobby lobby = lobbyWithInvited(invitedId);
        lobby.acceptInvite(invited);
    }

    private Lobby lobbyWithInvited(Id invitedId) {
        Optional<Lobby> lobby = lobbies.stream()
                .filter(l -> l.getPendingInvites().anyMatch(u -> u.id().equals(invitedId)))
                .findAny();
        return lobby.orElseThrow(() ->
                new RacegridException(RacegridError.LOBBY_NOT_FOUND, "User is not invited to any lobby: " + invitedId));
    }

    public void declineInvite(Id invitedId) {
        assertUserExists(invitedId);
        Lobby lobby = lobbyWithInvited(invitedId);
        lobby.removePendingInvite(invitedId);
    }

    public void undoInvite(Id userId, Id invitedId) {
        assertUserExists(userId);
        assertUserExists(invitedId);
        Lobby lobby = lobbyHostedBy(userId);
        lobby.removePendingInvite(invitedId);
    }

    public void kickPlayer(Id userId, Id otherUserId) {
        assertUserExists(userId);
        assertUserExists(otherUserId);
        Lobby lobby = lobbyHostedBy(userId);
        lobby.removeUser(otherUserId);
    }

    public void leaveLobby(Id userId) {
        assertUserExists(userId);
        Lobby lobby = lobbyWithMember(userId);
        lobby.removeUser(userId);
        boolean anyUserLeft = lobby.getUsers().findAny().isPresent();
        if (!anyUserLeft) {
            lobbies.remove(lobby);
        }
    }

    private Lobby lobbyWithMember(Id userId) {
        Optional<Lobby> lobby = lobbies.stream()
                .filter(l -> l.hasUser(userId))
                .findAny();
        return lobby.orElseThrow(() ->
                new RacegridException(RacegridError.LOBBY_NOT_FOUND, "No lobby with that user: " + userId));
    }
}
