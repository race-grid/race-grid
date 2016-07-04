package racegrid.game;

import racegrid.model.Line;
import racegrid.model.Vector;

import java.util.Optional;
import java.util.stream.Stream;

public interface Terrain {

    Optional<Vector> collisionBetween(Vector from, Vector to);

    Stream<Line> getGraphicLines();
}
