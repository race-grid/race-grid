package racegrid.game;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import racegrid.model.Collision;
import racegrid.model.CollisionType;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.RacegridException;
import racegrid.model.Vector;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(JUnit4.class)
public class GameBoardTest {

    private CollisionHandler collisionHandler = mock(CollisionHandler.class);
    private MutableGameBoard board;
    private Id ID = Id.of("ID");

    @Before
    public void setup(){
        board = new MutableGameBoard(collisionHandler);
    }

    @Test
    public void addPlayer(){
        board.addPlayer(new Player("p1", ID), new Vector(0, 0));
    }

    @Test(expected = RacegridException.class)
    public void addPlayer_shouldNotAllowDuplicatePlayerIds() {
        board.addPlayer(new Player("p1", ID), new Vector(0, 0));
        board.addPlayer(new Player("p2", ID), new Vector(0, 1));
    }

    @Test
    public void makeMove(){
        when(collisionHandler.collisionBetween(any(), any())).thenReturn(Optional.empty());
        board.addPlayer(new Player("p1", ID), new Vector(0, 0));
        board.makeMove(ID, new Vector(1, 0));
    }

    @Test
    public void getPreviousPositions(){
        when(collisionHandler.collisionBetween(any(), any())).thenReturn(Optional.empty());
        board.addPlayer(new Player("p1", ID), new Vector(0, 0));
        board.makeMove(ID, new Vector(1, 0));
        List<Vector> moves = board.getPlayerPositionHistory(ID).collect(Collectors.toList());
        assertEquals(2, moves.size());
        assertEquals(new Vector(0, 0), moves.get(0));
        assertEquals(new Vector(1, 0), moves.get(1));
    }

    @Test
    public void makeMove_shouldSetProperPositionAfterCollision(){
        Collision collision = new Collision(null, new Vector(1, 0), CollisionType.WITH_WALL);
        Vector destination = new Vector(2, 0);
        when(collisionHandler.collisionBetween(any(), any())).thenReturn(Optional.of(collision));
        board.addPlayer(new Player("p1", ID), new Vector(0, 0));
        board.makeMove(ID, destination);
        assertEquals(collision.resultingPosition(), board.getPlayerCurrentPosition(ID));
    }

    @Test
    public void getPlayers() {
        Player player = new Player("p1", ID);
        board.addPlayer(player, new Vector(0, 0));
        List<Player> players = board.getPlayers().collect(Collectors.toList());
        assertEquals(1, players.size());
        assertEquals(player, players.get(0));
    }


}