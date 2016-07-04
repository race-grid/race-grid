package racegrid.game;

import racegrid.Geometry;
import racegrid.model.Line;
import racegrid.model.Vector;
import racegrid.model.ExactVector;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class LineTerrain implements Terrain {

    private final List<Line> walls;

    private LineTerrain(List<Line> walls) {
        this.walls = walls;
    }

    public static LineTerrain fromList(List<Line> lines) {
        return new LineTerrain(lines);
    }

    @Override
    public Optional<Vector> collisionBetween(Vector from, Vector to) {

        ExactVector exactFrom = ExactVector.of(from);
        ExactVector exactTo = ExactVector.of(to);

        Line movement = new Line(exactFrom, exactTo);
        Stream<ExactVector> wallIntersections = walls.stream()
                .map(wall -> Geometry.linesIntersect(movement, wall))
                .filter(Optional::isPresent)
                .map(Optional::get);
        Optional<ExactVector> collision = closestTo(wallIntersections, exactFrom);
        return collision.map(c -> Geometry.oneStepTowardsTarget(c, from));
    }

    @Override
    public Stream<Line> getGraphicLines(){
        return walls.stream();
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
