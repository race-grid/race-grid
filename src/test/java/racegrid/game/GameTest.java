package racegrid.game;

import org.junit.Assert;
import org.junit.Test;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.PlayerGameState;
import racegrid.model.RacegridException;
import racegrid.model.Vector;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GameTest {

    Player P1 = new Player("P1", Id.of("P1_ID"));
    Player P2 = new Player("P2", Id.of("P2_ID"));

    @Test(expected = RacegridException.class)
    public void makeMove_shouldNotAllowMoveAtOpponentsTurn() {
        MutableGameBoard board = new MutableGameBoard(Terrain.empty());
        board.addPlayer(P1, new Vector(0, 0));
        board.addPlayer(P2, new Vector(1, 0));
        Game game = new Game(board, P1.id());
        game.makeMove(P2.id(), new Vector(0, 1));
    }

    @Test(expected = RacegridException.class)
    public void makeMove_shouldNotAllowInvalidMove() {
        MutableGameBoard board = new MutableGameBoard(Terrain.empty());
        board.addPlayer(P1, new Vector(0, 0));
        board.addPlayer(P2, new Vector(1, 0));
        Game game = new Game(board, P1.id());
        game.makeMove(P1.id(), new Vector(100, 100));
    }

    @Test
    public void makeMove() {
        MutableGameBoard board = new MutableGameBoard(Terrain.empty());
        board.addPlayer(P1, new Vector(0, 0));
        board.addPlayer(P2, new Vector(1, 0));
        Game game = new Game(board, P1.id());
        game.makeMove(P1.id(), new Vector(0, 0));
        Map<Id, PlayerGameState> states = game.getBoard().getPlayerStates();
        assertEquals(2, states.size());
        Assert.assertEquals(P2.id(), game.getActivePlayerId());
        Assert.assertEquals(2, states.get(P1.id()).positionHistory().size());
        Assert.assertEquals(1, states.get(P2.id()).positionHistory().size());
    }

    @Test
    public void getValidMoves_shouldIndicateCollisions() {
        Vector blockedPos = new Vector(1, 0);
        Terrain terrain = mock(Terrain.class);
        MutableGameBoard board = new MutableGameBoard(terrain);
        board.addPlayer(new Player("p1", P1.id()), new Vector(0, 0));

        when(terrain.collisionBetween(any(), any())).thenReturn(Optional.empty());
        when(terrain.collisionBetween(any(), eq(blockedPos))).thenReturn(Optional.of(blockedPos));

        Game game = new Game(board, P1.id());
        Map<Vector, Optional<Vector>> moves = game.getValidMovesWithCollisionData(P1.id());

        assertEquals(9, moves.size());
        Assert.assertEquals(Optional.of(blockedPos), moves.get(blockedPos));
    }

}