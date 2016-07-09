package racegrid.api;

import racegrid.api.model.Line;
import racegrid.api.model.Vector;
import racegrid.api.model.ExactVector;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Geometry {

    public static List<Vector> withSurrounding(Vector center) {
        return Arrays.asList(
                center,
                center.plus(0, -1),
                center.plus(1, -1),
                center.plus(1, 0),
                center.plus(1, 1),
                center.plus(0, 1),
                center.plus(-1, 1),
                center.plus(-1, 0),
                center.plus(-1, -1));
    }

    public static double sqDistanceBetween(ExactVector a, ExactVector b) {
        double dx = b.x() - a.x();
        double dy = b.y() - a.y();
        return dx * dx + dy * dy;
    }

    public static Optional<ExactVector> linesIntersect(Line a, Line b) {
        return Optional.empty(); //TODO
    }

    /**
     * Compute the result of stepping toward a target from given source.
     * NOTE: How to handle cases where the resulting position is not free?
     */
    public static Vector oneStepTowardsTarget(ExactVector source, Vector target) {
        return null; //TODO
    }
}
