package nl.uva.td.game;

public class PlayerAttributes {

    private int mLives;

    private double mGold;

    public PlayerAttributes(final int lives, final double gold) {
        mLives = lives;
        mGold = gold;
    }

    public int getLives() {
        return mLives;
    }

    public void setLives(final int lives) {
        mLives = lives;
    }

    public double getGold() {
        return mGold;
    }

    public void setGold(final double gold) {
        mGold = gold;
    }
}
