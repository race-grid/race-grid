package racegrid.game;

import org.junit.Test;
import racegrid.game.gameRunner.GameRunnerFactory;
import racegrid.game.gameRunner.PlayerAi;
import racegrid.game.gameRunner.TimebasedGameRunner;
import racegrid.model.GameSettings;
import racegrid.model.GameState;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.RaceTrack;
import racegrid.model.RacegridException;
import racegrid.model.Vector;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class TimebasedGameRunnerTest {

    private final String USER_NAME = "NAME";
    private final Id USER_ID = Id.of("ID");
    private final Player PLAYER = new Player(USER_NAME, USER_ID);
    private final GameSettings SETTINGS = new GameSettings(1000);
    private final RaceTrack TRACK = new RaceTrack(
            Collections.emptyList(),
            10,
            10,
            null,
            Arrays.asList(new Vector(1, 1), new Vector(2, 2))
    );
    private final GameBotSettings BOT_SETTINGS = new GameBotSettings(Collections.singletonList("A"), new PlayerAi());

    @Test
    public void startGame() {
        TimebasedGameRunner game = GameRunnerFactory.timeBasedVsAi(TRACK, PLAYER, BOT_SETTINGS, SETTINGS);
        game.startGame();
    }

    @Test(expected = RacegridException.class)
    public void startGame_shouldNotAllowStartingTwice() {
        TimebasedGameRunner game = GameRunnerFactory.timeBasedVsAi(TRACK, PLAYER, BOT_SETTINGS, SETTINGS);
        game.startGame();
        game.startGame();
    }

    @Test(expected = RacegridException.class)
    public void makeMove_shouldThrowIfGameNotStarted() {
        TimebasedGameRunner game = GameRunnerFactory.timeBasedVsAi(TRACK, PLAYER, BOT_SETTINGS, SETTINGS);
        game.userMakeMove(USER_ID, new Vector(0, 0));
    }

    @Test
    public void getGameState() {
        TimebasedGameRunner game = GameRunnerFactory.timeBasedVsAi(TRACK, PLAYER, BOT_SETTINGS, SETTINGS);
        GameState state = game.getGameState();
        assertEquals(2, state.playerStates().size());
        assertEquals(USER_ID, state.activePlayerId());
    }

    @Test(expected = RacegridException.class)
    public void makeMove_shouldNotWorkWhenTurnExpired() throws InterruptedException {
        GameSettings settings = new GameSettings(50);
        TimebasedGameRunner game = GameRunnerFactory.timeBasedVsAi(TRACK, PLAYER, BOT_SETTINGS, settings);
        game.startGame();
        Thread.sleep(settings.turnDurationMillis() + 10);
        game.userMakeMove(USER_ID, new Vector(0, 0));
    }
}