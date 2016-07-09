package racegrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import racegrid.model.Collision;
import racegrid.model.RacegridException;
import racegrid.model.GameSettings;
import racegrid.model.GameState;
import racegrid.model.Id;
import racegrid.model.NewUserResponse;
import racegrid.model.UserAuth;
import racegrid.model.Vector;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EngineTest {
    private final String USER_NAME = "userName";
    private final Id UNKNOWN_ID = Id.of("UNKNOWN_ID");
    private final UUID UNKNOWN_HASH = UUID.randomUUID();
    private GameSettings SETTINGS = new GameSettings(5);
    private TrackRepository trackRepository = new TrackRepository(new ObjectMapper());
    private UserRepository userRepository = new UserRepository();
    private GameRepository gameRepository = new GameRepository(userRepository);
    private Engine engine = new Engine(trackRepository, userRepository, gameRepository);

    @Test(expected = RacegridException.class)
    public void getValidMovesWithCollisionData_shouldThrowForUnknownUser() {
        engine.getValidMovesWithCollisionData(UNKNOWN_ID, new UserAuth(UNKNOWN_ID, UNKNOWN_HASH));
    }

    @Test(expected = RacegridException.class)
    public void getValidMovesWithCollisionData_shouldThrowForUnknownGame() {
        NewUserResponse newUserResponse = engine.newUser(USER_NAME);
        engine.getValidMovesWithCollisionData(UNKNOWN_ID, authFromNewUser(newUserResponse));
    }

    @Test
    public void getValidMovesWithCollisionData() {
        NewUserResponse response = engine.newUser(USER_NAME);
        UserAuth auth = authFromNewUser(response);

        Id gameId = engine.newTimedGameVsAi(auth, 1, SETTINGS);
        Map<Vector, Optional<Collision>> validMoves = engine.getValidMovesWithCollisionData(gameId, auth);
        assertEquals(9, validMoves.size());
    }

    @Test
    public void newGameVsAi() {
        NewUserResponse response = engine.newUser(USER_NAME);
        Id gameId = engine.newTimedGameVsAi(authFromNewUser(response), 1, SETTINGS);
        assertNotNull(gameId);
    }

    @Test
    public void newSlowGameVsAi() {
        NewUserResponse response = engine.newUser(USER_NAME);
        Id gameId = engine.newSlowGameVsAi(authFromNewUser(response), 1);
        assertNotNull(gameId);
    }

    @Test(expected = RacegridException.class)
    public void makeMove_shouldThrowForUnknownUser() {
        UserAuth auth = new UserAuth(UNKNOWN_ID, UNKNOWN_HASH);
        engine.userMakeMove(UNKNOWN_ID, auth, new Vector(0, 0));
    }

    @Test(expected = RacegridException.class)
    public void makeMove_shouldThrowForUnknownGame() {
        NewUserResponse response = engine.newUser(USER_NAME);
        UserAuth auth = authFromNewUser(response);
        engine.userMakeMove(UNKNOWN_ID, auth, new Vector(0, 0));
    }

    @Test
    public void makeMove() {
        NewUserResponse response = engine.newUser(USER_NAME);
        UserAuth auth = authFromNewUser(response);
        Id gameId = engine.newTimedGameVsAi(auth, 2, new GameSettings(5));
        engine.startTimedGame(gameId);
        GameState state = engine.userMakeMove(gameId, auth, new Vector(10, 5));
        assertNotNull(state);
    }

    private UserAuth authFromNewUser(NewUserResponse response) {
        return new UserAuth(response.getUser().id(), response.getUserHash());
    }

    public void test() {
        NewUserResponse response = engine.newUser("jnoathan");

        UserAuth auth = new UserAuth(response.getUser().id(), response.getUserHash());
        Id gameId = engine.newSlowGameVsAi(auth, 2);
        GameState gameState = engine.getGameState(gameId);
        engine.getValidMovesWithCollisionData(gameId, auth);
    }
}