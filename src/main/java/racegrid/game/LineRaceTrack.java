package racegrid.game;

import racegrid.Geometry;
import racegrid.model.Line;
import racegrid.model.RaceTrackData;
import racegrid.model.Vector;
import racegrid.model.ExactVector;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class LineRaceTrack implements RaceTrack {

    private final RaceTrackData trackData;

    public LineRaceTrack(RaceTrackData trackData) {
        this.trackData = trackData;
    }

    @Override
    public Optional<Vector> collisionBetween(Vector from, Vector to) {

        ExactVector exactFrom = ExactVector.of(from);
        ExactVector exactTo = ExactVector.of(to);

        Line movement = new Line(exactFrom, exactTo);
        Stream<ExactVector> wallIntersections = trackData.walls().stream()
                .map(wall -> Geometry.linesIntersect(movement, wall))
                .filter(Optional::isPresent)
                .map(Optional::get);
        Optional<ExactVector> collision = closestTo(wallIntersections, exactFrom);
        return collision.map(c -> Geometry.oneStepTowardsTarget(c, from));
    }

    @Override
    public RaceTrackData getData() {
        return trackData;
    }

    private Optional<ExactVector> closestTo(Stream<ExactVector> others, ExactVector target) {
        Comparator<ExactVector> byClosestToPos = (a, b) -> {
            double aSqDistance = Geometry.sqDistanceBetween(a, target);
            double bSqDistance = Geometry.sqDistanceBetween(b, target);
            return (int) (bSqDistance - aSqDistance);
        };
        return others.min(byClosestToPos);
    }
}
