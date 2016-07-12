package racegrid.api.game;

import racegrid.api.model.Collision;
import racegrid.api.model.Id;
import racegrid.api.model.Player;
import racegrid.api.model.PlayerGameState;
import racegrid.api.model.Vector;

import java.util.ArrayList;
import java.util.Optional;

public class MutableGameBoard extends GameBoard {

    public MutableGameBoard(CollisionHandler collisionHandler) {
        super(collisionHandler);
    }

    public void addPlayer(Player player, Vector position) {
        assertHasNoPlayerWithGivenId(player.id());
        PlayerGameState state = new PlayerGameState(player, new ArrayList<>(), false);
        state.positionHistory().add(position);
        playerStates.put(player.id(), state);
    }

    public void makeMove(Id playerId, Vector destination) {
        Vector from = getPlayerCurrentPosition(playerId);
        PlayerGameState state = playerStates.get(playerId);

        Optional<Collision> collision = collisionHandler.collisionBetween(from, destination);

        Vector actualNewPos = collision
                .map(Collision::resultingPosition)
                .orElse(destination);
        state.positionHistory().add(actualNewPos);

        boolean playerPassedGoalLine = collisionHandler.passingGoalLine(from, actualNewPos);
        if(playerPassedGoalLine){
            state.setHasFinished();
        }
    }
}
