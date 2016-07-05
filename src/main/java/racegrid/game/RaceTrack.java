package racegrid.game;

import racegrid.model.RaceTrackData;
import racegrid.model.Vector;

import java.util.Optional;

public interface RaceTrack {

    Optional<Vector> collisionBetween(Vector from, Vector to);

    RaceTrackData getData();
}
