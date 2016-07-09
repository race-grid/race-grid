package racegrid.api.game;

import org.junit.Test;
import racegrid.api.model.Collision;
import racegrid.api.model.CollisionType;
import racegrid.api.model.ExactVector;
import racegrid.api.model.Id;
import racegrid.api.model.Line;
import racegrid.api.model.Player;
import racegrid.api.model.PlayerGameState;
import racegrid.api.model.RaceTrack;
import racegrid.api.model.RacegridException;
import racegrid.api.model.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class GameTest {

    Player P1 = new Player("P1", Id.of("P1_ID"));
    Player P2 = new Player("P2", Id.of("P2_ID"));

    private RaceTrack track() {
        List<Line> walls = Collections.singletonList(new Line(
                new ExactVector(0.5, 0),
                new ExactVector(0.5, 2)));
        List<Vector> startPositions = Arrays.asList(new Vector(1, 1), new Vector(1, 3));
        return new RaceTrack(walls, 10, 10, null, startPositions);
    }

    private CollisionHandler collisionHandler() {
        return new CollisionHandler(track());
    }

    @Test(expected = RacegridException.class)
    public void makeMove_shouldNotAllowMoveAtOpponentsTurn() {
        CollisionHandler collisionHandler = collisionHandler();
        MutableGameBoard board = new MutableGameBoard(collisionHandler);
        board.addPlayer(P1, new Vector(0, 0));
        board.addPlayer(P2, new Vector(1, 0));
        Game game = new Game(board, P1.id());
        game.makeMove(P2.id(), new Vector(0, 1));
    }

    @Test(expected = RacegridException.class)
    public void makeMove_shouldNotAllowInvalidMove() {
        CollisionHandler collisionHandler = collisionHandler();
        MutableGameBoard board = new MutableGameBoard(collisionHandler);
        board.addPlayer(P1, new Vector(0, 0));
        board.addPlayer(P2, new Vector(1, 0));
        Game game = new Game(board, P1.id());
        game.makeMove(P1.id(), new Vector(100, 100));
    }

    @Test
    public void makeMove_shouldBeAbleToFinish() {
        CollisionHandler collisionHandler = mock(CollisionHandler.class);
        MutableGameBoard board = new MutableGameBoard(collisionHandler);
        board.addPlayer(P1, new Vector(0, 0));
        Game game = new Game(board, P1.id());

        doReturn(true).when(collisionHandler).passingGoalLine(any(), any());
        doReturn(Optional.empty()).when(collisionHandler).collisionBetween(any(), any());

        assertFalse(game.getBoard().getPlayerStates().get(P1.id()).hasFinished());
        game.makeMove(P1.id(), new Vector(1, 1));
        assertTrue(game.getBoard().getPlayerStates().get(P1.id()).hasFinished());

    }

    @Test
    public void makeMove() {
        RaceTrack track = track();
        CollisionHandler collisionHandler = new CollisionHandler(track);
        MutableGameBoard board = new MutableGameBoard(collisionHandler);
        board.addPlayer(P1, track.startPositions().get(0));
        board.addPlayer(P2, track.startPositions().get(1));
        Game game = new Game(board, P1.id());
        game.makeMove(P1.id(), new Vector(2, 1));
        Map<Id, PlayerGameState> states = game.getBoard().getPlayerStates();
        assertEquals(2, states.size());
        assertEquals(P2.id(), game.getActivePlayerId());
        assertEquals(2, states.get(P1.id()).positionHistory().size());
        assertEquals(1, states.get(P2.id()).positionHistory().size());
    }

    @Test
    public void getValidMoves_shouldIndicateCollisions() {
        Vector blockedPos = new Vector(1, 0);
        Collision collision = new Collision(new ExactVector(1, 0), new Vector(0, 0), CollisionType.WITH_WALL);
        CollisionHandler collisionHandler = mock(CollisionHandler.class);
        MutableGameBoard board = new MutableGameBoard(collisionHandler);
        board.addPlayer(new Player("p1", P1.id()), new Vector(0, 0));

        doReturn(Optional.empty()).when(collisionHandler).collisionBetween(any(), any());
        doReturn(Optional.of(collision)).when(collisionHandler).collisionBetween(any(), eq(blockedPos));

        Game game = new Game(board, P1.id());
        Map<Vector, Optional<Collision>> moves = game.getValidMovesWithCollisionData(P1.id());

        assertEquals(9, moves.size());
        assertEquals(Optional.of(collision), moves.get(blockedPos));
    }

}