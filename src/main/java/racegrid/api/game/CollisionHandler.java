package racegrid.api.game;

import racegrid.api.Geometry;
import racegrid.api.model.Collision;
import racegrid.api.model.ExactVector;
import racegrid.api.model.Line;
import racegrid.api.model.RaceTrack;
import racegrid.api.model.Vector;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class CollisionHandler {

    private final RaceTrack track;

    public CollisionHandler(RaceTrack track) {
        this.track = track;
    }

    public boolean passingGoalLine(Vector from, Vector to) {
        Line movement = new Line(ExactVector.of(from), ExactVector.of(to));
        return Geometry.linesIntersect(movement, track.goalLine()).isPresent();
    }

    public Optional<Collision> collisionBetween(Vector from, Vector to) {
        Collision collision = carCollision(from, to)
                .orElseGet(() -> wallCollision(from, to)
                        .orElse(null));
        if (collision != null) {
            return Optional.of(collision);
        }
        return Optional.empty();
    }

    private Optional<Collision> carCollision(Vector from, Vector to) {
        return Optional.empty();//TODO
    }

    private Optional<Collision> wallCollision(Vector from, Vector to) {
        ExactVector exactFrom = ExactVector.of(from);
        ExactVector exactTo = ExactVector.of(to);
        Line movement = new Line(exactFrom, exactTo);

        Stream<ExactVector> wallIntersections = track.walls().stream()
                .map(wall -> Geometry.linesIntersect(movement, wall))
                .filter(Optional::isPresent)
                .map(Optional::get);

        Optional<ExactVector> collisionLocation = closest(wallIntersections, exactFrom);
        return collisionLocation.map(cl -> {
            Vector resultingPosition = resultingPosition(from, cl);
            return Collision.withWall(cl, resultingPosition);
        });
    }

    private Vector resultingPosition(Vector from, ExactVector collision) {
        return null; //TODO
    }

    private Optional<ExactVector> closest(Stream<ExactVector> others, ExactVector target) {
        Comparator<ExactVector> byClosestToPos = (a, b) -> {
            double aSqDistance = Geometry.sqDistanceBetween(a, target);
            double bSqDistance = Geometry.sqDistanceBetween(b, target);
            return (int) (bSqDistance - aSqDistance);
        };
        return others.min(byClosestToPos);
    }
}
