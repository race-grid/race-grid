package racegrid.game;

import org.junit.Test;
import racegrid.game.gameRunner.SlowGameRunner;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.PlayerGameState;
import racegrid.model.Vector;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SlowGameRunnerTest {

    private SlowGameRunner game;
    private final Id ID = Id.of("ABC");

    @Test
    public void vsAi() {
        Player player = new Player("NAME", ID);
        game = SlowGameRunner.vsAi(player, 2);
        Map<Id, PlayerGameState> states = game.getGameState().playerStates();
        assertEquals(3, states.size());
    }

    @Test
    public void makeMove_shouldGoThroughAllBotsAfterMove() {
        Player player = new Player("NAME", ID);
        game = SlowGameRunner.vsAi(player, 2);
        assertEquals(ID, game.getGameState().activePlayerId());
        game.userMakeMove(ID, new Vector(0, 1));
        assertEquals(ID, game.getGameState().activePlayerId());
        Map<Id, PlayerGameState> states = game.getGameState().playerStates();
        states.values().forEach(state -> {
            assertEquals(2, state.positionHistory().size());
        });
    }

}