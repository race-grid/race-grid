package racegrid.api.service;

import org.junit.Before;
import org.junit.Test;
import racegrid.api.model.Id;
import racegrid.api.model.NewUserResponse;
import racegrid.api.model.RacegridException;
import racegrid.api.model.User;
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
        lobbyRepository = new LobbyRepository();
    }

    @Test
    public void createLobby() {
        User host = createNewUser(NAME);
        Id lobbyId = lobbyRepository.createLobby(host);
        assertEquals(host, lobbyById(lobbyId).getHost());
    }

    @Test
    public void getLobbies() {
        List<Lobby> lobbies = lobbyRepository.getLobbies().collect(Collectors.toList());
        assertEquals(0, lobbies.size());

        User host = createNewUser(NAME);
        lobbyRepository.createLobby(host);

        lobbies = lobbyRepository.getLobbies().collect(Collectors.toList());
        assertEquals(1, lobbies.size());
        assertEquals(host, lobbies.get(0).getHost());
    }

    @Test(expected = RacegridException.class)
    public void inviteToLobby_shouldThrowWhenNotInLobby() {
        User host = createNewUser(NAME);
        User otherUser = createNewUser(NAME_2);
        lobbyRepository.inviteToLobby(host.id(), otherUser);
    }

    @Test
    public void inviteToLobby() {
        User host = createNewUser(NAME);
        User otherUser = createNewUser(NAME_2);
        createLobbyAndInvite(host, otherUser);
    }

    @Test
    public void acceptInvite() {
        User host = createNewUser(NAME);
        User otherUser = createNewUser(NAME_2);
        createLobbyInviteAndAccept(host, otherUser);
    }

    @Test
    public void declineInvite() {
        User host = createNewUser(NAME);
        User otherUser = createNewUser(NAME_2);
        createLobbyAndInvite(host, otherUser);
        lobbyRepository.declineInvite(otherUser.id());
    }

    @Test
    public void undoInvite() {
        User host = createNewUser(NAME);
        User otherUser = createNewUser(NAME_2);
        createLobbyAndInvite(host, otherUser);
        lobbyRepository.undoInvite(host.id(), otherUser.id());
    }

    @Test
    public void kickPlayer() {
        User host = createNewUser(NAME);
        User otherUser = createNewUser(NAME_2);
        createLobbyInviteAndAccept(host, otherUser);
        lobbyRepository.kickUser(host.id(), otherUser.id());
    }

    @Test(expected = RacegridException.class)
    public void leaveLobby_shouldThrowIfNotInLobby() {
        User host = createNewUser(NAME);
        lobbyRepository.leaveLobby(host.id());
    }

    @Test
    public void leaveLobby_shouldSetNewHost() {
        User host = createNewUser(NAME);
        User otherUser = createNewUser(NAME_2);
        Lobby lobby = createLobbyInviteAndAccept(host, otherUser);
        lobbyRepository.leaveLobby(host.id());
        assertEquals(otherUser, lobby.getHost());
    }

    @Test
    public void leaveLobby_shouldRemoveLobbyIfAlone() {
        User host = createNewUser(NAME);
        Id lobbyId = lobbyRepository.createLobby(host);
        lobbyRepository.leaveLobby(host.id());
        boolean lobbyRemoved = lobbyRepository.getLobbies()
                .noneMatch(l -> l.getId().equals(lobbyId));
        assertTrue(lobbyRemoved);
    }

    private void createLobbyAndInvite(User host, User otherUser) {
        lobbyRepository.createLobby(host);
        lobbyRepository.inviteToLobby(host.id(), otherUser);
    }

    private Lobby createLobbyInviteAndAccept(User host, User otherUser) {
        Id lobbyId = lobbyRepository.createLobby(host);
        lobbyRepository.inviteToLobby(host.id(), otherUser);
        lobbyRepository.acceptInvite(otherUser);
        return lobbyById(lobbyId);
    }

    private Lobby lobbyById(Id lobbyId) {
        return lobbyRepository.getLobbies()
                .filter(l -> l.getId().equals(lobbyId))
                .findAny()
                .orElseThrow(RuntimeException::new);
    }

    private User createNewUser(String name) {
        NewUserResponse response = userRepository.newUser(name);
        return response.getUser();
    }

}