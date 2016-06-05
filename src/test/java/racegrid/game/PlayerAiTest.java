package racegrid.game;

import org.junit.Before;
import org.junit.Test;
import racegrid.game.gameRunner.PlayerAi;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class PlayerAiTest {

    private PlayerAi ai = new PlayerAi();
    private Id ID = Id.of("ID");
    private Player player = new Player("p1", ID);
    private MutableGameBoard board;

    @Before
    public void setup(){
        List<Vector> blockedPositions = Arrays.asList(new Vector(1, 0));
        Terrain terrain = Terrain.fromList(blockedPositions);
        board = new MutableGameBoard(terrain);
    }

    @Test
    public void getMove(){
        board.addPlayer(player, new Vector(0, 0));
        List<Vector> validMoves = Collections.singletonList(new Vector(0, 1));
        Vector destination = ai.getNextMove(player.id(), validMoves, board);
        assertTrue(validMoves.contains(destination));
    }

}