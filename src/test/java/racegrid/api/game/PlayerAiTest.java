package racegrid.api.game;

import org.junit.Before;
import org.junit.Test;
import racegrid.api.game.gameRunner.PlayerAi;
import racegrid.api.model.Id;
import racegrid.api.model.Player;
import racegrid.api.model.RaceTrack;
import racegrid.api.model.Vector;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PlayerAiTest {

    private PlayerAi ai = new PlayerAi();
    private Id ID = Id.of("ID");
    private Player player = new Player("p1", ID);
    private CollisionHandler collisionHandler;
    private MutableGameBoard board;

    @Before
    public void setup() {
        collisionHandler = mock(CollisionHandler.class);
        RaceTrack track = mock(RaceTrack.class);
        board = new MutableGameBoard(track, collisionHandler);
    }

    @Test
    public void getMove() {
        board.addPlayer(player, new Vector(0, 0));
        List<Vector> validMoves = Collections.singletonList(new Vector(0, 1));
        Vector destination = ai.getNextMove(player.id(), validMoves, board);
        assertTrue(validMoves.contains(destination));
    }

}