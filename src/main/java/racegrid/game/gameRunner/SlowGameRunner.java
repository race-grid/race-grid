package racegrid.game.gameRunner;

import racegrid.game.Game;
import racegrid.game.MutableGameBoard;
import racegrid.game.Terrain;
import racegrid.model.GameState;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SlowGameRunner extends AbstractGameRunner implements GameRunner {


    private SlowGameRunner(Game game, Map<Id, PlayerAi> bots) {
        super(game, bots);
    }

    public static SlowGameRunner players(List<Player> players) {
        MutableGameBoard board = new MutableGameBoard(Terrain.empty());
        for (int i = 0; i < players.size(); i++) {
            Vector startPosition = new Vector(i, 0);
            board.addPlayer(players.get(i), startPosition);
        }
        Game game = new Game(board, players.get(0).id());
        return new SlowGameRunner(game, null);
    }

    public static SlowGameRunner vsAi(Player player, int numOpponents) {
        Map<Id, PlayerAi> bots = new HashMap<>();
        MutableGameBoard board = createBotsAndGameboard(player, bots, numOpponents);
        Game game = new Game(board, player.id());
        return new SlowGameRunner(game, bots);
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
