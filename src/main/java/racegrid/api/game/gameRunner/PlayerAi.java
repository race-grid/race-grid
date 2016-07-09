package racegrid.api.game.gameRunner;

import racegrid.api.game.GameBoard;
import racegrid.api.model.Id;
import racegrid.api.model.Vector;

import java.util.Collection;

public class PlayerAi {

    public Vector getNextMove(Id playerId, Collection<Vector> validMoves, GameBoard board) {
        return validMoves.iterator().next();
    }
}
