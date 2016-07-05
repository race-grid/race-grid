package racegrid.game;

import racegrid.model.RaceTrackData;
import racegrid.model.Vector;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BlockRaceTrack implements RaceTrack {

    private final List<Vector> blockedPositions;

    private BlockRaceTrack(List<Vector> blockedPositions) {
        this.blockedPositions = blockedPositions;
    }

    public static BlockRaceTrack empty() {
        return new BlockRaceTrack(Collections.emptyList());
    }

    public static BlockRaceTrack fromList(List<Vector> blockedPositions) {
        return new BlockRaceTrack(blockedPositions);
    }

    @Override
    public Optional<Vector> collisionBetween(Vector from, Vector to) {
        if (blockedPositions.contains(to)) {
            return Optional.of(to);
        }
        return Optional.empty();
    }

    @Override
    public RaceTrackData getData() {
        return null; //TODO
    }


}
