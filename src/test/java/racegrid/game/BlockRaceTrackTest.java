package racegrid.game;

import org.junit.Test;
import racegrid.model.Vector;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;

public class BlockRaceTrackTest {

    private BlockRaceTrack track = BlockRaceTrack.fromList(Collections.singletonList(new Vector(1, 0)));

    @Test
    public void isValidMove(){
        Optional<Vector> collision = track.collisionBetween(new Vector(0, 0), new Vector(1, 0));
        assertEquals(Optional.of(new Vector(1, 0)), collision);
    }

}