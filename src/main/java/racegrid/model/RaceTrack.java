package racegrid.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RaceTrack {

    private final List<Line> walls;
    private final int height;
    private final int width;
    private final Line goalLine;
    private final List<Vector> startPositions;

    @JsonCreator
    public RaceTrack(@JsonProperty("walls") List<Line> walls,
                     @JsonProperty("height") int height,
                     @JsonProperty("width") int width,
                     @JsonProperty("goalLine") Line goalLine,
                     @JsonProperty("startPositions") List<Vector> startPositions) {
        this.walls = walls;
        this.height = height;
        this.width = width;
        this.goalLine = goalLine;
        this.startPositions = startPositions;
    }

    @JsonProperty
    public List<Line> walls() {
        return walls;
    }

    @JsonProperty
    public int height() {
        return height;
    }

    @JsonProperty
    public int width() {
        return width;
    }

    @JsonProperty
    public Line goalLine() {
        return goalLine;
    }

    @JsonProperty
    public List<Vector> startPositions() {
        return startPositions;
    }
}
