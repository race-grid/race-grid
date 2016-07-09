package racegrid.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Vector {

    private final int x;
    private final int y;

    @JsonCreator
    public Vector(@JsonProperty("x") int x,
                  @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }

    @JsonProperty
    public int x() {
        return x;
    }

    @JsonProperty
    public int y() {
        return y;
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

    @Override
    public int hashCode() {
        return x * 31 + y;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Vector)) {
            return false;
        }
        Vector p = (Vector) o;
        return x == p.x && y == p.y;
    }

    public Vector plus(Vector other) {
        return new Vector(x + other.x, y + other.y);
    }

    public Vector plus(int dx, int dy) {
        return new Vector(x + dx, y + dy);
    }

    public Vector minus(Vector other) {
        return new Vector(x - other.x, y - other.y);
    }

    public Vector minus(int dx, int dy) {
        return new Vector(x - dx, y - dy);
    }
}
