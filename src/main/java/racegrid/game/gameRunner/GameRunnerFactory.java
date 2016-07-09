package racegrid.game.gameRunner;

import racegrid.game.CollisionHandler;
import racegrid.game.Game;
import racegrid.game.GameBotSettings;
import racegrid.game.MutableGameBoard;
import racegrid.model.GameSettings;
import racegrid.model.Id;
import racegrid.model.Player;
import racegrid.model.RaceTrack;
import racegrid.model.Vector;

import java.util.HashMap;
import java.util.Map;

public class GameRunnerFactory {

    public static TimebasedGameRunner timeBasedVsAi(RaceTrack track, Player player, GameBotSettings aiSettings, GameSettings settings) {
        GameAndBots data = setupGameAndBots(track, player, aiSettings);
        return new TimebasedGameRunner(data.game, data.bots, settings);
    }

    public static SlowGameRunner slowVsAi(RaceTrack track, Player player, GameBotSettings aiSettings) {
        GameAndBots data = setupGameAndBots(track, player, aiSettings);
        return new SlowGameRunner(data.game, data.bots);
    }

    private static GameAndBots setupGameAndBots(RaceTrack track, Player player, GameBotSettings aiSettings) {
        assertEnoughStartPositions(track, aiSettings.botNames().size() + 1);

        CollisionHandler collisionHandler = new CollisionHandler(track);
        MutableGameBoard board = new MutableGameBoard(collisionHandler);
        Vector playerStartPos = track.startPositions().get(0);
        board.addPlayer(player, playerStartPos);

        Map<Id, PlayerAi> bots = new HashMap<>();
        for (int i = 0; i < aiSettings.botNames().size(); i++) {
            String botName = aiSettings.botNames().get(i);
            Vector startPos = track.startPositions().get(i + 1);
            Player aiPlayer = new Player(botName, Id.generateUnique());
            board.addPlayer(aiPlayer, startPos);
            bots.put(aiPlayer.id(), aiSettings.ai());
        }

        Game game = new Game(board, player.id());
        return new GameAndBots(game, bots);
    }

    private static class GameAndBots {
        private final Game game;
        private final Map<Id, PlayerAi> bots;

        public GameAndBots(Game game, Map<Id, PlayerAi> bots) {
            this.game = game;
            this.bots = bots;
        }

        public Game game() {
            return game;
        }

        public Map<Id, PlayerAi> bots() {
            return bots;
        }
    }


    private static void assertEnoughStartPositions(RaceTrack track, int minNumber) {
        if (track.startPositions().size() < minNumber) {
            throw new IllegalArgumentException("Track has less than " + minNumber + " start positions: " + track);
        }
    }

}
