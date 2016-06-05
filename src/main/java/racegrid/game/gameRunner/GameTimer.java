package racegrid.game.gameRunner;

import racegrid.model.RacegridException;

public class GameTimer {

    private final long turnDurationMillis;

    private long currentTurnStartedAt;
    private boolean gameHasStarted;
    private Thread timerThread;
    private final Runnable callback;


    public GameTimer(long turnDurationMillis, Runnable timeRanOutCallback) {
        this.turnDurationMillis = turnDurationMillis;
        this.callback = timeRanOutCallback;
    }

    public boolean hasGameStarted() {
        return gameHasStarted;
    }

    public long getTimeRemainingMillis() {
        long passed = System.currentTimeMillis() - currentTurnStartedAt;
        return turnDurationMillis - passed;
    }

    public void startGame() {
        if(gameHasStarted) {
            throw new RacegridException("Can't start game twice!");
        }
        gameHasStarted = true;
        startCountdown();
    }

    public void stopCountdown() {
        if(timerThread != null) {
            timerThread.interrupt();
        }
    }

    public void startCountdown() {
        timerThread = new Thread(this::timerTask);
        timerThread.start();
    }

    private void timerTask() {
        try {
            currentTurnStartedAt = System.currentTimeMillis();
            Thread.sleep(turnDurationMillis);
            callback.run();
        } catch (InterruptedException e) {
            System.out.println("TIMER TASK INTERRUPTED!");
        }
    }

}
