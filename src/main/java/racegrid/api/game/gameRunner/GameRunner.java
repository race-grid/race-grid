package racegrid.api.game.gameRunner;

import racegrid.api.game.GameBoard;
import racegrid.api.model.Collision;
import racegrid.api.model.GameState;
import racegrid.api.model.Id;
import racegrid.api.model.Vector;

import java.util.Map;
import java.util.Optional;

public interface GameRunner {
    GameState userMakeMove(Id playerId, Vector destination);
    GameState getGameState();
    Map<Vector, Optional<Collision>> getValidMovesWithCollisionData(Id playerId);
    GameBoard getBoard();
}
