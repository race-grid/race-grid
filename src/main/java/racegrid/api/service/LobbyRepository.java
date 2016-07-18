package racegrid.api.service;

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

    private List<Lobby> lobbies = new ArrayList<>();

    public Id createLobby(User host) {
        Id lobbyId = Id.generateUnique();
        Lobby newLobby = new Lobby(lobbyId, host);
        lobbies.add(newLobby);
        return lobbyId;
    }

    public void inviteToLobby(Id userId, User invited) {
        Lobby lobby = lobbyHostedBy(userId);
        lobby.addPendingInvite(invited);
    }

    private Lobby lobbyHostedBy(Id hostId) {
        return lobbies.stream()
                .filter(l -> l.getHost().id().equals(hostId))
                .findAny()
                .orElseThrow(() -> new RacegridException(RacegridError.LOBBY_NOT_FOUND, "User is not host of a lobby"));
    }

    public Stream<Lobby> getLobbies() {
        return lobbies.stream();
    }

    public Lobby lobbyById(Id lobbyId) {
        return lobbies.stream()
                .filter(l -> l.getId().equals(lobbyId))
                .findAny()
                .orElseThrow(() -> new RacegridException(RacegridError.LOBBY_NOT_FOUND, "No lobby with id: " + lobbyId));
    }

    public void acceptInvite(User invited) {
        Lobby lobby = lobbyWithInvited(invited.id());
        lobby.acceptInvite(invited);
    }

    private Lobby lobbyWithInvited(Id invitedId) {
        Optional<Lobby> lobby = lobbies.stream()
                .filter(l -> l.hasPendingInvite(invitedId))
                .findAny();
        return lobby.orElseThrow(() ->
                new RacegridException(RacegridError.LOBBY_NOT_FOUND, "User is not invited to any lobby: " + invitedId));
    }

    public void declineInvite(Id invitedId) {
        Lobby lobby = lobbyWithInvited(invitedId);
        lobby.removePendingInvite(invitedId);
    }

    public void undoInvite(Id userId, Id invitedId) {
        Lobby lobby = lobbyHostedBy(userId);
        lobby.removePendingInvite(invitedId);
    }

    public void kickUser(Id userId, Id otherUserId) {
        Lobby lobby = lobbyHostedBy(userId);
        lobby.removeUser(otherUserId);
    }

    public void leaveLobby(Id userId) {
        Lobby lobby = lobbyWithMember(userId);
        lobby.removeUser(userId);
        if (lobby.isEmpty()) {
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
