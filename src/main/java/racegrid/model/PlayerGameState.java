package racegrid.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class PlayerGameState {
    private final Player player;
    private final List<Vector> positionHistory;

    @JsonCreator
    public PlayerGameState(@JsonProperty("player") Player player,
                           @JsonProperty("positionHistory") List<Vector> positionHistory) {
        this.player = player;
        this.positionHistory = positionHistory;
    }

    @JsonProperty
    public Player player() {
        return player;
    }

    @JsonProperty
    public List<Vector> positionHistory() {
        return positionHistory;
    }

    @JsonIgnore
    public PlayerGameState getCopy() {
        return new PlayerGameState(player, new ArrayList<>(positionHistory));
    }
}