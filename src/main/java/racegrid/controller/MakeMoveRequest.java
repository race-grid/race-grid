package racegrid.controller;

import racegrid.model.Id;
import racegrid.model.Vector;

import java.util.UUID;

public class MakeMoveRequest {
    private Id gameId;
    private Id userId;
    private UUID userHash;
    private Vector destination;

    public Id getGameId() {
        return gameId;
    }

    public void setGameId(Id gameId) {
        this.gameId = gameId;
    }

    public Id getUserId() {
        return userId;
    }

    public void setUserId(Id userId) {
        this.userId = userId;
    }

    public UUID getUserHash() {
        return userHash;
    }

    public void setUserHash(UUID userHash) {
        this.userHash = userHash;
    }

    public Vector getDestination() {
        return destination;
    }

    public void setDestination(Vector destination) {
        this.destination = destination;
    }
}
