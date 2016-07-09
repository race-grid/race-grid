package racegrid.game;

import racegrid.Geometry;
import racegrid.model.Collision;
import racegrid.model.ExactVector;
import racegrid.model.Line;
import racegrid.model.RaceTrack;
import racegrid.model.Vector;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class CollisionHandler {

    private final RaceTrack trackData;

    public CollisionHandler(RaceTrack trackData) {
        this.trackData = trackData;
    }

    public Optional<Collision> collisionBetween(Vector from, Vector to){
        Collision collision = carCollision(from, to)
                .orElseGet(() -> wallCollision(from, to)
                        .orElse(null));
        if (collision != null) {
            return Optional.of(collision);
        }
        return Optional.empty();
    }

    private Optional<Collision> carCollision(Vector from, Vector to){
        return Optional.empty();//TODO
    }

    private Optional<Collision> wallCollision(Vector from, Vector to){
        ExactVector exactFrom = ExactVector.of(from);
        ExactVector exactTo = ExactVector.of(to);
        Line movement = new Line(exactFrom, exactTo);

        Stream<ExactVector> wallIntersections = trackData.walls().stream()
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
