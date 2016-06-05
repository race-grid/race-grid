package racegrid.game;

import racegrid.model.Vector;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Terrain {

    private final List<Vector> blockedPositions;

    private Terrain(List<Vector> blockedPositions) {
        this.blockedPositions = blockedPositions;
    }

    public static Terrain empty(){
        return new Terrain(Collections.emptyList());
    }

    public static Terrain fromList(List<Vector> blockedPositions) {
        return new Terrain(blockedPositions);
    }

    public Optional<Vector> collisionBetween(Vector from, Vector to) {
        if( blockedPositions.contains(to)){
            return Optional.of(to);
        }
        return Optional.empty();
    }
}
