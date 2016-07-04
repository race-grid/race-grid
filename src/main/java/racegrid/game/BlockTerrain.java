package racegrid.game;

import racegrid.model.Line;
import racegrid.model.Vector;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class BlockTerrain implements Terrain{

    private final List<Vector> blockedPositions;

    private BlockTerrain(List<Vector> blockedPositions) {
        this.blockedPositions = blockedPositions;
    }

    public static BlockTerrain empty(){
        return new BlockTerrain(Collections.emptyList());
    }

    public static BlockTerrain fromList(List<Vector> blockedPositions) {
        return new BlockTerrain(blockedPositions);
    }

    @Override
    public Optional<Vector> collisionBetween(Vector from, Vector to) {
        if( blockedPositions.contains(to)){
            return Optional.of(to);
        }
        return Optional.empty();
    }

    @Override
    public Stream<Line> getGraphicLines() {
        return Stream.empty(); //TODO
    }


}
