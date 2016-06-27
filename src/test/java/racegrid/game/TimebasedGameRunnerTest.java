package racegrid.game;

import org.junit.Before;
import org.junit.Test;
import racegrid.game.gameRunner.TimebasedGameRunner;
import racegrid.model.GameSettings;
import racegrid.model.GameState;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.RacegridException;
import racegrid.model.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TimebasedGameRunnerTest {

    private final String USER_NAME = "NAME";
    private final Id USER_ID = Id.of("ID");
    private final Player PLAYER = new Player(USER_NAME, USER_ID);
    private final GameSettings SETTINGS = new GameSettings(1000);

    @Before
    public void setup() {


    }

    @Test
    public void startGame() {
        TimebasedGameRunner game = TimebasedGameRunner.vsAi(PLAYER, 1, SETTINGS);
        game.startGame();
    }

    @Test(expected = RacegridException.class)
    public void startGame_shouldNotAllowStartingTwice() {
        TimebasedGameRunner game = TimebasedGameRunner.vsAi(PLAYER, 1, SETTINGS);
        game.startGame();
        game.startGame();
    }

    @Test(expected = RacegridException.class)
    public void makeMove_shouldThrowIfGameNotStarted() {
        TimebasedGameRunner game = TimebasedGameRunner.vsAi(PLAYER, 1, SETTINGS);
        game.userMakeMove(USER_ID, new Vector(0, 0));
    }

    @Test
    public void getGameState() {
        TimebasedGameRunner game = TimebasedGameRunner.vsAi(PLAYER, 1, SETTINGS);
        GameState state = game.getGameState();
        assertEquals(2,  state.playerStates().size());
        assertEquals(USER_ID, state.activePlayerId());
    }

    @Test(expected = RacegridException.class)
    public void makeMove_shouldNotWorkWhenTurnExpired() throws InterruptedException {
        GameSettings settings = new GameSettings(50);
        TimebasedGameRunner game = TimebasedGameRunner.vsAi(PLAYER, 2, settings);
        game.startGame();
        Thread.sleep(settings.turnDurationMillis() + 10);
        game.userMakeMove(USER_ID, new Vector(0, 0));
    }
}