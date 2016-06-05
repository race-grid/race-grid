package racegrid.game;

import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.PlayerGameState;
import racegrid.model.RacegridException;
import racegrid.model.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameBoard {


    final Terrain terrain;
    final HashMap<Id, PlayerGameState> playerStates = new HashMap<>();

    public GameBoard(Terrain terrain) {
        this.terrain = terrain;
    }

    void assertHasNoPlayerWithGivenId(Id id) {
        boolean somePlayerHasId = playerStates.keySet().stream()
                .anyMatch(existingId -> existingId.equals(id));
        if (somePlayerHasId) {
            throw new RacegridException("Already has player with id: " + id);
        }
    }

    public Vector playerCurrentPosition(Id playerId) {
        List<Vector> history = playerStates.get(playerId).positionHistory();
        return history.get(history.size() - 1);
    }

    public Terrain getTerrain(){
        return terrain;
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

    public Vector getPlayerPositionNStepsAgo(Id playerId, int n){
        List<Vector> positionHistory = playerStates.get(playerId).positionHistory();
        if(n < 0 || n >= positionHistory.size()){
            throw new IllegalArgumentException("Bad input n: " + n + ", positionHistory.size(): " + positionHistory.size());
        }
        return positionHistory.get(positionHistory.size() - 1 - n);
    }
}
