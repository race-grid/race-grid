package racegrid.model;

public class GameSettings {
    private final int turnDurationMillis;

    public GameSettings(int turnDurationMillis) {
        this.turnDurationMillis = turnDurationMillis;
    }

    public int turnDurationMillis() {
        return turnDurationMillis;
    }
}
