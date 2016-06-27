package racegrid.game;

import racegrid.model.Vector;

import java.util.Optional;

public interface Terrain {
    Optional<Vector> collisionBetween(Vector from, Vector to);
}
