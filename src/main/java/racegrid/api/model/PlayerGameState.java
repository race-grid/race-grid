package racegrid.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PlayerGameState {
    private final Player player;
    private final List<Vector> positionHistory;
    private boolean hasFinished;

    @JsonCreator
    public PlayerGameState(@JsonProperty("player") Player player,
                           @JsonProperty("positionHistory") List<Vector> positionHistory,
                           @JsonProperty("hasFinished") boolean hasFinished) {
        this.player = player;
        this.positionHistory = positionHistory;
        this.hasFinished = hasFinished;
    }

    @JsonProperty
    public Player player() {
        return player;
    }

    @JsonProperty
    public List<Vector> positionHistory() {
        return positionHistory;
    }

    public void setHasFinished() {
        hasFinished = true;
    }

    @JsonProperty
    public boolean hasFinished() {
        return hasFinished;
    }

    @JsonIgnore
    public PlayerGameState getCopy() {
        return new PlayerGameState(player, new ArrayList<>(positionHistory), hasFinished);
    }
}