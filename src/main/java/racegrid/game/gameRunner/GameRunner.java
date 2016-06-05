package racegrid.game.gameRunner;

import racegrid.game.GameBoard;
import racegrid.model.GameState;
import racegrid.model.Id;
import racegrid.model.Vector;

import java.util.Map;
import java.util.Optional;

public interface GameRunner {
    GameState userMakeMove(Id playerId, Vector destination);
    GameState getGameState();
    Map<Vector, Optional<Vector>> getValidMovesWithCollisionData(Id playerId);
    GameBoard getBoard();
}
