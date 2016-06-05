package racegrid.game.gameRunner;

import racegrid.game.Game;
import racegrid.game.GameBoard;
import racegrid.game.MutableGameBoard;
import racegrid.game.Terrain;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.Vector;

import java.util.Map;
import java.util.Optional;

public class AbstractGameRunner {
    final Game game;
    final Map<Id, PlayerAi> bots;

    AbstractGameRunner(Game game, Map<Id, PlayerAi> bots) {
        this.game = game;
        this.bots = bots;
    }

    public Map<Vector, Optional<Vector>> getValidMovesWithCollisionData(Id playerId) {
        return game.getValidMovesWithCollisionData(playerId);
    }

    static MutableGameBoard createBotsAndGameboard(Player userPlayer, Map<Id, PlayerAi> bots, int numOpponents) {
        MutableGameBoard board = new MutableGameBoard(Terrain.empty());
        Vector playerStartPos = new Vector(0, 0);
        board.addPlayer(userPlayer, playerStartPos);
        for (int i = 0; i < numOpponents; i++) {
            String name = "AI-" + (i + 1);
            Vector startPos = new Vector(i + 1, 0);
            Player aiPlayer = new Player(name, Id.generateUnique());
            board.addPlayer(aiPlayer, startPos);
            bots.put(aiPlayer.id(), new PlayerAi());
        }
        return board;
    }

    public GameBoard getBoard(){
        return game.getBoard();
    }
}
