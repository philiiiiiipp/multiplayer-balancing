package nl.uva.td.ai;

public class GameInfo {

    private final Policy mPlayedAgainst;

    private final int mSteps;

    public GameInfo(final Policy playedAgainst, final int steps) {
        mPlayedAgainst = playedAgainst;
        mSteps = steps;
    }

    /**
     * The policy which was played against
     *
     * @return The policy which was played against
     */
    public Policy getPlayedAgainst() {
        return mPlayedAgainst;
    }

    /**
     * The steps this game took
     *
     * @return The total amount of steps this game took
     */
    public int getSteps() {
        return mSteps;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof GameInfo) {
            return ((GameInfo) o).mPlayedAgainst.equals(mPlayedAgainst);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return mPlayedAgainst.hashCode();
    }

    @Override
    public String toString() {
        return mPlayedAgainst.toString();
    }
}
