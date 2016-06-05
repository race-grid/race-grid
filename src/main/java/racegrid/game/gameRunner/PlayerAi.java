package racegrid.game.gameRunner;

import racegrid.game.GameBoard;
import racegrid.model.Id;
import racegrid.model.Vector;

import java.util.Collection;

public class PlayerAi {

    public Vector getNextMove(Id playerId, Collection<Vector> validMoves, GameBoard board) {
        return validMoves.iterator().next();
    }
}
