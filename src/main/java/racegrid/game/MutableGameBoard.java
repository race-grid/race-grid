package racegrid.game;

import racegrid.model.Collision;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.PlayerGameState;
import racegrid.model.Vector;

import java.util.ArrayList;
import java.util.Optional;

public class MutableGameBoard extends GameBoard {

    public MutableGameBoard(CollisionHandler collisionHandler) {
        super(collisionHandler);
    }

    public void addPlayer(Player player, Vector position) {
        assertHasNoPlayerWithGivenId(player.id());
        PlayerGameState data = new PlayerGameState(player, new ArrayList<>());
        data.positionHistory().add(position);
        playerStates.put(player.id(), data);
    }

    public void makeMove(Id id, Vector destination) {
        Optional<Collision> collision = collisionHandler.collisionBetween(getPlayerCurrentPosition(id), destination);
        Vector actualNewPos = collision
                .map(Collision::resultingPosition)
                .orElse(destination);
        playerStates.get(id).positionHistory().add(actualNewPos);
    }

}
