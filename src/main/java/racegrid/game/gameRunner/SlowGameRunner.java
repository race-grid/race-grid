package racegrid.game.gameRunner;

import racegrid.game.Game;
import racegrid.model.GameState;
import racegrid.model.Id;
import racegrid.model.Vector;

import java.util.Map;
import java.util.Set;

public class SlowGameRunner extends AbstractGameRunner implements GameRunner {

    private final Map<Id, PlayerAi> bots;

    SlowGameRunner(Game game, Map<Id, PlayerAi> bots) {
        super(game);
        this.bots = bots;
    }

    @Override
    public GameState userMakeMove(Id playerId, Vector destination) {
        game.makeMove(playerId, destination);
        GameState state = getGameState();
        botMakeMoves();
        return state;
    }

    private void botMakeMoves() {
        Id activeId = game.getActivePlayerId();
        while (bots.containsKey(activeId)) {
            Set<Vector> validMoves = game.getValidMovesWithCollisionData(activeId).keySet();
            Vector botMove = bots.get(activeId).getNextMove(activeId, validMoves, game.getBoard());
            game.makeMove(activeId, botMove);
            activeId = game.getActivePlayerId();
        }
    }

    @Override
    public GameState getGameState() {
        return GameState.slow(game.getActivePlayerId(), game.getBoard().getPlayerStates());
    }

}
