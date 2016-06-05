package racegrid.game;

import org.junit.Test;
import racegrid.model.Vector;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;

public class TerrainTest {

    private Terrain terrain = Terrain.fromList(Arrays.asList(new Vector(1, 0)));

    @Test
    public void isValidMove(){
        Optional<Vector> collision = terrain.collisionBetween(new Vector(0, 0), new Vector(1, 0));
        assertEquals(Optional.of(new Vector(1, 0)), collision);
    }

}