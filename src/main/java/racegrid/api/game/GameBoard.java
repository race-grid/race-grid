package racegrid.api.game;

import racegrid.api.model.Id;
import racegrid.api.model.Player;
import racegrid.api.model.PlayerGameState;
import racegrid.api.model.RaceTrack;
import racegrid.api.model.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameBoard {

    protected final HashMap<Id, PlayerGameState> playerStates = new HashMap<>();
    protected final CollisionHandler collisionHandler;
    protected final RaceTrack raceTrack;

    public GameBoard(RaceTrack raceTrack, CollisionHandler collisionHandler) {
        this.raceTrack = raceTrack;
        this.collisionHandler = collisionHandler;
    }

    public Vector getPlayerCurrentPosition(Id playerId) {
        List<Vector> history = playerStates.get(playerId).positionHistory();
        return history.get(history.size() - 1);
    }

    public Stream<Vector> getPlayerPositionHistory(Id playerId) {
        return playerStates.get(playerId).positionHistory().stream();
    }

    public Stream<Player> getPlayers() {
        return playerStates.values().stream().map(PlayerGameState::player);
    }

    public Map<Id, PlayerGameState> getPlayerStates() {
        return playerStates.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> e.getValue().getCopy()
        ));
    }

    public Stream<Id> getPlayerIds() {
        return playerStates.keySet().stream();
    }

    public Vector getPlayerPreviousPosition(Id playerId) {
        List<Vector> positionHistory = playerStates.get(playerId).positionHistory();
        int index = Math.max(0, positionHistory.size() - 1);
        return positionHistory.get(index);
    }

    public CollisionHandler getCollisionHandler() {
        return collisionHandler;
    }

    public RaceTrack getRaceTrack() {
        return raceTrack;
    }
}
