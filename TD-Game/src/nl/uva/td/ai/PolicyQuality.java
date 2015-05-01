package nl.uva.td.ai;

import java.util.HashSet;

public class PolicyQuality {

    private final HashSet<Policy> mBeats = new HashSet<Policy>();

    private final HashSet<Policy> mDraws = new HashSet<Policy>();

    private final HashSet<Policy> mLoses = new HashSet<Policy>();

    /**
     * Adds a policy which gets beaten from this policy
     *
     * @param policy
     */
    public void beats(final Policy policy) {
        mBeats.add(policy);
    }

    /**
     * Add a policy which draws against this policy
     *
     * @param policy
     */
    public void draws(final Policy policy) {
        mDraws.add(policy);
    }

    /**
     * Adds a policy which wins against this policy
     *
     * @param policy
     */
    public void loses(final Policy policy) {
        mLoses.add(policy);
    }

    public HashSet<Policy> getBeats() {
        return mBeats;
    }

    public HashSet<Policy> getDraws() {
        return mDraws;
    }

    public HashSet<Policy> getLoses() {
        return mLoses;
    }

    public boolean isGoodQuality() {
        int winPlusDraw = mBeats.size() + mDraws.size();

        if (mBeats.size() >= mLoses.size() || winPlusDraw > mLoses.size()) {
            return true;
        }

        if (mBeats.size() > 3) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        String result = "Beats:\n";
        for (Policy p : mBeats) {
            result += p + "\n";
        }
        result += "Draws:\n";
        for (Policy p : mDraws) {
            result += p + "\n";
        }
        result += "Loses:\n";
        for (Policy p : mLoses) {
            result += p + "\n";
        }

        return result;
    }

}
