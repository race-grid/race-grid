package racegrid.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import racegrid.api.game.GameBotSettings;
import racegrid.api.game.gameRunner.GameRunner;
import racegrid.api.game.gameRunner.GameRunnerFactory;
import racegrid.api.game.gameRunner.PlayerAi;
import racegrid.api.game.gameRunner.SlowGameRunner;
import racegrid.api.game.gameRunner.TimebasedGameRunner;
import racegrid.api.model.Collision;
import racegrid.api.model.GameEntry;
import racegrid.api.model.GameSettings;
import racegrid.api.model.GameState;
import racegrid.api.model.Id;
import racegrid.api.model.NewUserResponse;
import racegrid.api.model.Player;
import racegrid.api.model.RaceTrack;
import racegrid.api.model.RacegridError;
import racegrid.api.model.RacegridException;
import racegrid.api.model.User;
import racegrid.api.model.UserAuth;
import racegrid.api.model.Vector;
import racegrid.model.Lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class Engine {

    private final static String TRACK_1_FILE_PATH = "/race-track-1.json";


    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final LobbyRepository lobbyRepository;
    private final HashMap<Id, GameRunner> games = new HashMap<>();

    @Autowired
    public Engine(TrackRepository trackRepository,
                  UserRepository userRepository,
                  GameRepository gameRepository,
                  LobbyRepository lobbyRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.lobbyRepository = lobbyRepository;
    }

    public NewUserResponse newUser(String name) {
        return userRepository.newUser(name);
    }

    public Id newTimedGameVsAi(UserAuth auth, int numOpponents, GameSettings settings) {
        User user = assertUserExistsAndAuthenticated(auth);
        RaceTrack track = track();
        GameEntry game = gameRepository.newGame(Collections.singletonList(user));
        Player player = new Player(user.name(), user.id());
        TimebasedGameRunner gameRunner = GameRunnerFactory.timeBasedVsAi(track, player, botSettings(numOpponents), settings);
        games.put(game.id(), gameRunner);
        return game.id();
    }

    private RaceTrack track() {
        return trackRepository.loadTrackFromFile(TRACK_1_FILE_PATH);
    }

    private GameBotSettings botSettings(int numBots) {
        List<String> botNames = new ArrayList<>();
        for (int i = 0; i < numBots; i++) {
            botNames.add("BOT-" + (i + 1));
        }
        return new GameBotSettings(botNames, new PlayerAi());
    }


    public Id newSlowGameVsAi(UserAuth auth, int numOpponents) {
        User user = assertUserExistsAndAuthenticated(auth);
        RaceTrack track = track();
        GameEntry game = gameRepository.newGame(Collections.singletonList(user));
        Player player = new Player(user.name(), user.id());
        SlowGameRunner gameRunner = GameRunnerFactory.slowVsAi(track, player, botSettings(numOpponents));
        games.put(game.id(), gameRunner);
        return game.id();
    }

    public void startTimedGame(Id gameId) {
        GameRunner game = assertGameExists(gameId);
        if (game instanceof TimebasedGameRunner) {
            ((TimebasedGameRunner) game).startGame();
        } else {
            throw new RacegridException(RacegridError.CAN_NOT_START_GAME, "This game is not time-based and cannot be 'started'");
        }
    }

    public Stream<GameEntry> getGames() {
        return gameRepository.getGames();
    }

    public Stream<User> getUsers() {
        return userRepository.getUsers();
    }

    public GameState userMakeMove(Id gameId, UserAuth auth, Vector destination) {
        assertUserExistsAndAuthenticated(auth);
        GameRunner game = assertGameExists(gameId);
        return game.userMakeMove(auth.id(), destination);
    }

    public Map<Vector, Optional<Collision>> getValidMovesWithCollisionData(Id gameId, UserAuth auth) {
        User user = assertUserExistsAndAuthenticated(auth);
        GameRunner game = assertGameExists(gameId);
        return game.getValidMovesWithCollisionData(user.id());
    }

    public GameState getGameState(Id gameId) {
        GameRunner game = assertGameExists(gameId);
        return game.getGameState();
    }

    public Id createLobby(UserAuth auth) {
        User user = assertUserExistsAndAuthenticated(auth);
        return lobbyRepository.createLobby(user);
    }

    public void inviteToLobby(UserAuth hostAuth, Id invitedId) {
        User host = assertUserExistsAndAuthenticated(hostAuth);
        User invited = assertUserExists(invitedId);
        assertUserNotInGame(invitedId);
        lobbyRepository.inviteToLobby(host.id(), invited);
    }

    public void acceptInvite(UserAuth auth) {
        User user = assertUserExistsAndAuthenticated(auth);
        lobbyRepository.acceptInvite(user);
    }

    public void declineInvite(UserAuth auth) {
        User user = assertUserExistsAndAuthenticated(auth);
        lobbyRepository.declineInvite(user.id());
    }

    public void undoInvite(UserAuth hostAuth, Id otherUserId) {
        User host = assertUserExistsAndAuthenticated(hostAuth);
        assertUserExists(otherUserId);
        lobbyRepository.undoInvite(host.id(), otherUserId);
    }

    public void kickFromLobby(UserAuth hostAuth, Id otherUserId) {
        User host = assertUserExistsAndAuthenticated(hostAuth);
        assertUserExists(otherUserId);
        lobbyRepository.kickUser(host.id(), otherUserId);
    }

    public void leaveLobby(UserAuth auth) {
        User user = assertUserExistsAndAuthenticated(auth);
        lobbyRepository.leaveLobby(user.id());
    }

    public Lobby lobbyById(Id lobbyId) {
        return lobbyRepository.lobbyById(lobbyId);
    }

    private GameRunner assertGameExists(Id gameId) {
        if (!gameRepository.gameById(gameId).isPresent()) {
            throw new RacegridException(RacegridError.GAME_NOT_FOUND, "No game with id " + gameId);
        }
        return games.get(gameId);
    }

    private User assertUserExists(Id userId) {
        Optional<User> user = userRepository.userById(userId);
        return user.orElseThrow(() ->
                new RacegridException(RacegridError.USER_NOT_FOUND, "That user doesn't exist: " + userId)
        );
    }

    private void assertUserNotInGame(Id userId) {
        boolean isInGame = gameRepository.isUserInGame(userId);
        if (isInGame) {
            throw new RacegridException(RacegridError.INTERNAL, "User is in game");
        }
    }

    private User assertUserExistsAndAuthenticated(UserAuth auth) {
        User user = userRepository.userById(auth.id())
                .orElseThrow(() -> new RacegridException(RacegridError.USER_NOT_FOUND, "User with id " + auth.id() + " not found"));
        boolean authenticated = userRepository.authenticateUser(auth);
        if (!authenticated) {
            throw new RacegridException(RacegridError.AUTHENTICATION_ERROR, "Authentication error!");
        }
        return user;
    }

    public Stream<Lobby> getLobbies() {
        return lobbyRepository.getLobbies();
    }

    public void removeUser(UserAuth auth) {
        assertUserExistsAndAuthenticated(auth);
        userRepository.removeUser(auth.id());
    }
}
