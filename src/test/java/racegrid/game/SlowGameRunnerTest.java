package racegrid.game;

import org.junit.Test;
import racegrid.game.gameRunner.GameRunnerFactory;
import racegrid.game.gameRunner.PlayerAi;
import racegrid.game.gameRunner.SlowGameRunner;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.PlayerGameState;
import racegrid.model.RaceTrack;
import racegrid.model.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SlowGameRunnerTest {

    private final RaceTrack TRACK = new RaceTrack(
            Collections.emptyList(),
            10,
            10,
            null,
            Arrays.asList(new Vector(1, 1), new Vector(2, 2), new Vector(3, 3))
    );

    private SlowGameRunner game;
    private final Id ID = Id.of("ABC");
    private final Player PLAYER = new Player("NAME", ID);
    private final GameBotSettings BOT_SETTINGS = new GameBotSettings(Arrays.asList("A", "B"), new PlayerAi());

    @Test
    public void vsAi() {
        game = GameRunnerFactory.slowVsAi(TRACK, PLAYER, BOT_SETTINGS);
        Map<Id, PlayerGameState> states = game.getGameState().playerStates();
        assertEquals(3, states.size());
    }

    @Test
    public void makeMove_shouldGoThroughAllBotsAfterMove() {
        game = GameRunnerFactory.slowVsAi(TRACK, PLAYER, BOT_SETTINGS);
        assertEquals(ID, game.getGameState().activePlayerId());
        game.userMakeMove(ID, new Vector(0, 1));
        assertEquals(ID, game.getGameState().activePlayerId());
        Map<Id, PlayerGameState> states = game.getGameState().playerStates();
        states.values().forEach(state ->
                assertEquals(2, state.positionHistory().size())
        );
    }

}