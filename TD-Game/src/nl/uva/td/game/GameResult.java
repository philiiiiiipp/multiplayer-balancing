package nl.uva.td.game;

import nl.uva.td.game.GameManager.Player;

public class GameResult {

    private Player mWinner;

    private final int mSteps;

    private int mMultiplier = 1;

    public GameResult(final Player winner, final int steps) {
        mWinner = winner;
        mSteps = steps;
    }

    public void setWinner(final Player winner) {
        mWinner = winner;
    }

    public Player getWinner() {
        return mWinner;
    }

    public int getSteps() {
        return mSteps;
    }

    public int getMultiplier() {
        return mMultiplier;
    }

    public void setMultiplier(final int multiplier) {
        mMultiplier = multiplier;
    }

}
