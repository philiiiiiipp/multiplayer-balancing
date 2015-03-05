package nl.uva.td.experiment;

public class Score {

    private final double mTotalSteps;

    private final double mLastStepTowerPoints;

    private final double mTotalTowerPoints;

    private final int mLastStepLivesLost;

    private final int mLivesLeft;

    private final double mGold;

    public Score(final double steps, final double lastStepTowerPoints, final double totalTowerPoints,
            final int lastStepLivesLost, final int livesLeft, final double gold) {
        mTotalSteps = steps;
        mLastStepTowerPoints = lastStepTowerPoints;
        mTotalTowerPoints = totalTowerPoints;
        mLastStepLivesLost = lastStepLivesLost;
        mLivesLeft = livesLeft;
        mGold = gold;
    }

    /**
     * Get the amount of steps the game ran in total
     *
     * @return The amount of steps the game ran
     */
    public double getSteps() {
        return mTotalSteps;
    }

    /**
     * Get all points the towers archived last round
     *
     * @return The amount of points the towers archived last round
     */
    public double getLastStepTowerPoints() {
        return mLastStepTowerPoints;
    }

    /**
     * Get the total amount of points the towers archived in this game so far
     *
     * @return The total amount of points archived so far by the towers
     */
    public double getTotalTowerPoints() {
        return mTotalTowerPoints;
    }

    /**
     * Get the amount of lives lost in the previous step
     *
     * @return The amount of lives lost in the previous step
     */
    public int getLastStepLivesLost() {
        return mLastStepLivesLost;
    }

    /**
     * Get the remaining lives of the player
     *
     * @return The remaining lives of the player
     */
    public int getLivesLeft() {
        return mLivesLeft;
    }

    public int getGold() {
        return (int) mGold;
    }

}
