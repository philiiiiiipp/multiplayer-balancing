package nl.uva.td.game;

public class PlayerAttributes {

    private int mLives;

    private int mGold;

    public PlayerAttributes(final int lives, final int gold) {
        mLives = lives;
        mGold = gold;
    }

    public int getLives() {
        return mLives;
    }

    public void setLives(final int lives) {
        mLives = lives;
    }

    public int getGold() {
        return mGold;
    }

    public void setGold(final int gold) {
        mGold = gold;
    }
}
