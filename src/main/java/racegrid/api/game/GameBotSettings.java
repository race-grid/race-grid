package racegrid.api.game;


import racegrid.api.game.gameRunner.PlayerAi;

import java.util.List;

public class GameBotSettings {
    private final List<String> botNames;
    private final PlayerAi ai;

    public GameBotSettings(List<String> botNames, PlayerAi ai) {
        this.botNames = botNames;
        this.ai = ai;
    }

    public List<String> botNames() {
        return botNames;
    }

    public PlayerAi ai() {
        return ai;
    }
}
