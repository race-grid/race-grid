package racegrid;

import org.junit.Test;
import racegrid.model.ExactVector;
import racegrid.model.Line;
import racegrid.model.Vector;

import java.util.Optional;

import static org.junit.Assert.*;

public class GeometryTest {

    @Test
    public void testLinesIntersect() {
        Line right = new Line(new ExactVector(-1, 0), new ExactVector(1, 0));
        Line down = new Line(new ExactVector(0, 1), new ExactVector(0, -1));
        Optional<ExactVector> intersection = Geometry.linesIntersect(right, down);
        assertTrue(intersection.isPresent());
        assertEquals(new ExactVector(0, 0), intersection.get());
    }

    @Test
    public void testLinesIntersect_variant() {
        Line left = new Line(new ExactVector(1, 0), new ExactVector(-1, 0));
        Line down = new Line(new ExactVector(0, 1), new ExactVector(0, -1));
        Optional<ExactVector> intersection = Geometry.linesIntersect(left, down);
        assertTrue(intersection.isPresent());
        assertEquals(new ExactVector(0, 0), intersection.get());
    }

    @Test
    public void testLinesIntersect_shouldNotIntersectSelf() {
        Line line = new Line(new ExactVector(-2, 3), new ExactVector(5, 1));
        Optional<ExactVector> intersection = Geometry.linesIntersect(line, line);
        assertFalse(intersection.isPresent());
    }

    @Test
    public void testLinesIntersect_shouldNotIntersectTouching() {
        Line a = new Line(new ExactVector(-1, 0), new ExactVector(0, 0));
        Line b = new Line(new ExactVector(0, 1), new ExactVector(0, 0));
        Optional<ExactVector> intersection = Geometry.linesIntersect(a, b);
        assertFalse(intersection.isPresent());
    }

    @Test
    public void testOneStepTowardsTarget_horizontal() {
        ExactVector source = new ExactVector(1.4, 1);
        Vector target = new Vector(0, 0);
        Vector result = Geometry.oneStepTowardsTarget(source, target);
        assertEquals(new Vector(1, 1), result);
    }

    @Test
    public void testOneStepTowardsTarget_vertical() {
        ExactVector source = new ExactVector(0.7, -2.8);
        Vector target = new Vector(0, 0);
        Vector result = Geometry.oneStepTowardsTarget(source, target);
        assertEquals(new Vector(1, -2), result);
    }

    @Test
    public void testOneStepTowardsTarget_diagonal() {
        ExactVector source = new ExactVector(0.6, 2.6);
        Vector target = new Vector(0, 2);
        Vector result = Geometry.oneStepTowardsTarget(source, target);
        assertEquals(new Vector(0, 2), result);
    }


}