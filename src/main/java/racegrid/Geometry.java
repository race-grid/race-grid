package racegrid;

import racegrid.model.Vector;

import java.util.Arrays;
import java.util.List;

public class Geometry {

    public static List<Vector> withSurrounding(Vector center) {
        return Arrays.asList(
                center,
                center.plus(0, -1),
                center.plus(1, -1),
                center.plus(1, 0),
                center.plus(1, 1),
                center.plus(0, 1),
                center.plus(-1, 1),
                center.plus(-1, 0),
                center.plus(-1, -1));
    }
}
