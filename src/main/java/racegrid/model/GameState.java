package racegrid.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.util.Map;

public class GameState {
    private final Id activePlayerId;
    private final Map<Id, PlayerGameState> playerStates;
    private final Long turnDurationRemainingMillis;

    @JsonCreator
    public GameState(
            @JsonProperty("activePlayerId") Id activePlayerId,
            @JsonProperty("playerStates") Map<Id, PlayerGameState> playerStates,
            @JsonProperty("turnDurationRemainingMillis") Long turnDurationRemainingMillis) {
        this.activePlayerId = activePlayerId;
        this.playerStates = playerStates;
        this.turnDurationRemainingMillis = turnDurationRemainingMillis;
    }

    public static GameState slow(Id activePlayerId, Map<Id, PlayerGameState> playerStates) {
        return new GameState(activePlayerId, playerStates, null);
    }

    public static GameState timed( Id activePlayerId,
                      Map<Id, PlayerGameState> playerStates,
                      long turnDurationRemainingMillis) {
        return new GameState(activePlayerId, playerStates, turnDurationRemainingMillis);
    }

    @JsonProperty
    public Id activePlayerId() {
        return activePlayerId;
    }

    @JsonProperty
    public Map<Id, PlayerGameState> playerStates() {
         return playerStates;
    }

    @JsonProperty
    public Long turnDurationRemainingMillis() {
        return turnDurationRemainingMillis;
    }
}
