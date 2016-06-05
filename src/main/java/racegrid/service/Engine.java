package racegrid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import racegrid.model.RacegridException;
import racegrid.game.AsciiBoard;
import racegrid.game.gameRunner.GameRunner;
import racegrid.game.gameRunner.SlowGameRunner;
import racegrid.game.gameRunner.TimebasedGameRunner;
import racegrid.model.GameEntry;
import racegrid.model.GameSettings;
import racegrid.model.GameState;
import racegrid.model.Id;
import racegrid.model.NewUserResponse;
import racegrid.model.Player;
import racegrid.model.User;
import racegrid.model.UserAuth;
import racegrid.model.Vector;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class Engine {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final HashMap<Id, GameRunner> games = new HashMap<>();

    @Autowired
    public Engine(UserRepository userRepository, GameRepository gameRepository) {
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
        TimebasedGameRunner gameRunner = TimebasedGameRunner.vsAi(player, numOpponents, settings);
        games.put(game.id(), gameRunner);
        return game.id();
    }

    public Id newSlowGameVsAi(UserAuth auth, int numOpponents){
        User user = assertUserExistsAndAuthorized(auth);
        GameEntry game = gameRepository.newGame(Collections.singletonList(user));
        Player player = new Player(user.name(), user.id());
        SlowGameRunner gameRunner = SlowGameRunner.vsAi(player, numOpponents);
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
        GameState state = game.userMakeMove(auth.id(), destination);
        printAsciiBoard(game); //TODO
        return state;
    }

    private void printAsciiBoard(GameRunner game){
        String ascii = AsciiBoard.boardToString(game.getBoard());
        System.out.println(ascii);
    }

    public Map<Vector, Optional<Vector>> getValidMovesWithCollisionData(Id gameId, UserAuth auth) {
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
