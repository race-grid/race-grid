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
import racegrid.api.model.RacegridException;
import racegrid.api.model.User;
import racegrid.api.model.UserAuth;
import racegrid.api.model.Vector;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class Engine {

    private final static String TRACK_1_FILE_PATH = "race-track-1.json";


    private final TrackRepository trackRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final HashMap<Id, GameRunner> games = new HashMap<>();

    @Autowired
    public Engine(TrackRepository trackRepository,
                  UserRepository userRepository,
                  GameRepository gameRepository) {
        this.trackRepository = trackRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public NewUserResponse newUser(String name) {
        return userRepository.newUser(name);
    }

    public Id newTimedGameVsAi(UserAuth auth, int numOpponents, GameSettings settings) {
        User user = assertUserExistsAndAuthorized(auth);
        GameEntry game = gameRepository.newGame(Collections.singletonList(user));
        Player player = new Player(user.name(), user.id());
        TimebasedGameRunner gameRunner = GameRunnerFactory.timeBasedVsAi(track(), player, botSettings(numOpponents), settings);
        games.put(game.id(), gameRunner);
        return game.id();
    }

    private RaceTrack track() {
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource(TRACK_1_FILE_PATH).getFile());
            return trackRepository.loadTrackFromFile(path);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RacegridException("Failed to load track data from file: " + TRACK_1_FILE_PATH);
        }
    }

    private GameBotSettings botSettings(int numBots) {
        List<String> botNames = new ArrayList<>();
        for (int i = 0; i < numBots; i++) {
            botNames.add("BOT-" + (i + 1));
        }
        return new GameBotSettings(botNames, new PlayerAi());
    }


    public Id newSlowGameVsAi(UserAuth auth, int numOpponents) {
        User user = assertUserExistsAndAuthorized(auth);
        GameEntry game = gameRepository.newGame(Collections.singletonList(user));
        Player player = new Player(user.name(), user.id());
        SlowGameRunner gameRunner = GameRunnerFactory.slowVsAi(track(), player, botSettings(numOpponents));
        games.put(game.id(), gameRunner);
        return game.id();
    }

    public void startTimedGame(Id gameId) {
        GameRunner game = assertGameExists(gameId);
        if (game instanceof TimebasedGameRunner) {
            ((TimebasedGameRunner) game).startGame();
        } else {
            throw new RacegridException("This game is not time-based and cannot be 'started'");
        }
    }

    private User assertUserExistsAndAuthorized(UserAuth auth) {
        User user = userRepository.userById(auth.id())
                .orElseThrow(() -> new RacegridException("User with id " + auth.id() + " not found"));
        boolean authorized = userRepository.authorizeUser(auth);
        if (!authorized) {
            throw new RacegridException("User not authorized!");
        }
        return user;
    }

    public Stream<GameEntry> getGames() {
        return gameRepository.getGames();
    }

    public Stream<User> getUsers() {
        return userRepository.getUsers();
    }

    public GameState userMakeMove(Id gameId, UserAuth auth, Vector destination) {
        assertUserExistsAndAuthorized(auth);
        GameRunner game = assertGameExists(gameId);
        return game.userMakeMove(auth.id(), destination);
    }

    public Map<Vector, Optional<Collision>> getValidMovesWithCollisionData(Id gameId, UserAuth auth) {
        User user = assertUserExistsAndAuthorized(auth);
        GameRunner game = assertGameExists(gameId);
        return game.getValidMovesWithCollisionData(user.id());
    }

    public GameState getGameState(Id gameId) {
        GameRunner game = assertGameExists(gameId);
        return game.getGameState();
    }

    private GameRunner assertGameExists(Id gameId) {
        if (!gameRepository.gameById(gameId).isPresent()) {
            throw new RacegridException("No game with id " + gameId);
        }
        return games.get(gameId);
    }
}
