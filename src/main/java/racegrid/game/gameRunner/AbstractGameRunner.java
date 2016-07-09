package racegrid.game.gameRunner;

import racegrid.game.Game;
import racegrid.game.GameBoard;
import racegrid.model.Collision;
import racegrid.model.Id;
import racegrid.model.RaceTrack;
import racegrid.model.Vector;

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

    protected static void assertEnoughStartPositions(RaceTrack track, int minNumber) {
        if (track.startPositions().size() < minNumber) {
            throw new IllegalArgumentException("Track has less than " + minNumber + " start positions: " + track);
        }
    }

    public GameBoard getBoard() {
        return game.getBoard();
    }
}
