package racegrid.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExactVector {

    private final double x;
    private final double y;

    @JsonCreator
    public ExactVector(@JsonProperty("x") double x,
                       @JsonProperty("y") double y) {
        this.x = x;
        this.y = y;
    }

    public static ExactVector of(Vector vector) {
        return new ExactVector(vector.x(), vector.y());
    }

    @JsonProperty
    public double x() {
        return x;
    }

    @JsonProperty
    public double y() {
        return y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
