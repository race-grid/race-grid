package racegrid.api.game.gameRunner;

import racegrid.api.game.Game;
import racegrid.api.model.*;

import java.util.Map;
import java.util.Set;

public class TimebasedGameRunner extends AbstractGameRunner implements GameRunner {

    private final GameTimer timer;
    private final Map<Id, PlayerAi> bots;

    TimebasedGameRunner(Game game, Map<Id, PlayerAi> bots, GameSettings settings) {
        super(game);
        this.timer = new GameTimer(settings.turnDurationMillis(), game::setNextPlayersTurn);
        this.bots = bots;
    }

    public void startGame() {
        timer.startGame();
    }

    private void botMakeMove() {
        Id playerId = game.getActivePlayerId();
        PlayerAi bot = bots.get(playerId);
        Set<Vector> validMoves = game.getValidMovesWithCollisionData(playerId).keySet();
        Vector nextPos = bot.getNextMove(playerId, validMoves, game.getBoard());
        makeMove(playerId, nextPos);
    }

    @Override
    public GameState userMakeMove(Id playerId, Vector destination) {
        assertGameStarted();
        return makeMove(playerId, destination);
    }

    @Override
    public GameState getGameState() {
        return GameState.timed(game.getActivePlayerId(), game.getBoard().getPlayerStates(), timer.getTimeRemainingMillis());
    }

    private GameState makeMove(Id playerId, Vector newPos) {
        timer.stopCountdown();
        game.makeMove(playerId, newPos);
        GameState state = getGameState();
        timer.startCountdown();
        boolean nextPlayerIsBot = bots.containsKey(game.getActivePlayerId());
        if (nextPlayerIsBot) {
            new Thread(this::botMakeMove).start();
        }
        return state;
    }

    private void assertGameStarted() {
        if (!timer.hasGameStarted()) {
            throw new RacegridException(RacegridError.GAME_NOT_RUNNING, "Game has not started!");
        }
    }

}
