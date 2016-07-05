package racegrid.model;

import java.util.List;

public class RaceTrackData {
    private final List<Line> walls;
    private final int height;
    private final int width;
    private final Line goalLine;

    public RaceTrackData(List<Line> walls, int height, int width, Line goalLine) {
        this.walls = walls;
        this.height = height;
        this.width = width;
        this.goalLine = goalLine;
    }

    public List<Line> walls() {
        return walls;
    }

    public int height() {
        return height;
    }

    public int width() {
        return width;
    }

    public Line goalLine() {
        return goalLine;
    }
}
