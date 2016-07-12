package racegrid.api.game.gameRunner;

import racegrid.api.game.Game;
import racegrid.api.game.GameBoard;
import racegrid.api.model.Collision;
import racegrid.api.model.Id;
import racegrid.api.model.RaceTrack;
import racegrid.api.model.Vector;

import java.util.Map;
import java.util.Optional;

public class AbstractGameRunner {
    protected final Game game;

    protected AbstractGameRunner(Game game) {
        this.game = game;
    }

    public Map<Vector, Optional<Collision>> getValidMovesWithCollisionData(Id playerId) {
        return game.getValidMovesWithCollisionData(playerId);
    }

    public GameBoard getBoard() {
        return game.getBoard();
    }
}
