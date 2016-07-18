package racegrid.api.service;

import org.junit.Before;
import org.junit.Test;
import racegrid.api.model.Id;
import racegrid.api.model.NewUserResponse;
import racegrid.api.model.RacegridException;
import racegrid.model.Lobby;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LobbyRepositoryTest {

    private final String NAME = "NAME";
    private final String NAME_2 = "OTHER_NAME";
    private UserRepository userRepository;
    private LobbyRepository lobbyRepository;

    @Before
    public void setup() {
        RacegridProps props = new RacegridProps();
        userRepository = new UserRepository(props);
        lobbyRepository = new LobbyRepository(userRepository);
    }

    @Test(expected = RacegridException.class)
    public void createLobby_shouldCheckCreatorExists() {
        Id hostId = Id.of("BAD_ID");
        lobbyRepository.createLobby(hostId);
    }

    @Test
    public void createLobby() {
        Id userId = createNewUser(NAME);
        Id lobbyId = lobbyRepository.createLobby(userId);
        assertEquals(userId, lobbyById(lobbyId).getHost().id());
    }

    @Test
    public void getLobbies() {
        List<Lobby> lobbies = lobbyRepository.getLobbies().collect(Collectors.toList());
        assertEquals(0, lobbies.size());

        Id userId = createNewUser(NAME);
        lobbyRepository.createLobby(userId);

        lobbies = lobbyRepository.getLobbies().collect(Collectors.toList());
        assertEquals(1, lobbies.size());
        assertEquals(userId, lobbies.get(0).getHost().id());
    }

    @Test(expected = RacegridException.class)
    public void inviteToLobby_shouldThrowWhenInvitedNotFound() {
        Id userId = createNewUser(NAME);
        createLobbyAndInvite(userId, Id.of("BAD_ID"));
    }

    @Test(expected = RacegridException.class)
    public void inviteToLobby_shouldThrowWhenNotInLobby() {
        Id userId = createNewUser(NAME);
        Id otherUserId = createNewUser(NAME_2);
        lobbyRepository.inviteToLobby(userId, otherUserId);
    }

    @Test
    public void inviteToLobby() {
        Id userId = createNewUser(NAME);
        Id otherUserId = createNewUser(NAME_2);
        createLobbyAndInvite(userId, otherUserId);
    }

    @Test
    public void acceptInvite() {
        Id userId = createNewUser(NAME);
        Id otherUserId = createNewUser(NAME_2);
        createLobbyInviteAndAccept(userId, otherUserId);
    }

    @Test
    public void declineInvite() {
        Id userId = createNewUser(NAME);
        Id otherUserId = createNewUser(NAME_2);
        createLobbyAndInvite(userId, otherUserId);
        lobbyRepository.declineInvite(otherUserId);
    }

    @Test
    public void undoInvite() {
        Id userId = createNewUser(NAME);
        Id otherUserId = createNewUser(NAME_2);
        createLobbyAndInvite(userId, otherUserId);
        lobbyRepository.undoInvite(userId, otherUserId);
    }

    @Test
    public void kickPlayer() {
        Id userId = createNewUser(NAME);
        Id otherUserId = createNewUser(NAME_2);
        createLobbyInviteAndAccept(userId, otherUserId);
        lobbyRepository.kickPlayer(userId, otherUserId);
    }

    @Test(expected = RacegridException.class)
    public void leaveLobby_shouldThrowIfNotInLobby() {
        Id userId = createNewUser(NAME);
        lobbyRepository.leaveLobby(userId);
    }

    @Test
    public void leaveLobby_shouldSetNewHost() {
        Id userId = createNewUser(NAME);
        Id otherUserId = createNewUser(NAME_2);
        Lobby lobby = createLobbyInviteAndAccept(userId, otherUserId);
        lobbyRepository.leaveLobby(userId);
        assertEquals(otherUserId, lobby.getHost().id());
    }

    @Test
    public void leaveLobby_shouldRemoveLobbyIfAlone() {
        Id userId = createNewUser(NAME);
        Id lobbyId = lobbyRepository.createLobby(userId);
        lobbyRepository.leaveLobby(userId);
        boolean lobbyRemoved = lobbyRepository.getLobbies()
                .noneMatch(l -> l.getId().equals(lobbyId));
        assertTrue(lobbyRemoved);
    }

    private void createLobbyAndInvite(Id userId, Id otherUserId) {
        lobbyRepository.createLobby(userId);
        lobbyRepository.inviteToLobby(userId, otherUserId);
    }

    private Lobby createLobbyInviteAndAccept(Id userId, Id otherUserId) {
        Id lobbyId = lobbyRepository.createLobby(userId);
        lobbyRepository.inviteToLobby(userId, otherUserId);
        lobbyRepository.acceptInvite(otherUserId);
        return lobbyById(lobbyId);
    }

    private Lobby lobbyById(Id lobbyId){
        return lobbyRepository.getLobbies()
                .filter(l -> l.getId().equals(lobbyId))
                .findAny()
                .orElseThrow(RuntimeException::new);
    }

    private Id createNewUser(String name) {
        NewUserResponse response = userRepository.newUser(name);
        return response.getUser().id();
    }

}