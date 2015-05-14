package nl.uva.td.ai;

import java.util.HashSet;
import java.util.Set;

public class PolicyQuality implements Comparable<PolicyQuality> {

    private final HashSet<GameInfo> mBeats = new HashSet<GameInfo>();

    private final HashSet<GameInfo> mDraws = new HashSet<GameInfo>();

    private final HashSet<GameInfo> mLoses = new HashSet<GameInfo>();

    /**
     * Adds a policy which gets beaten from this policy
     *
     * @param policy
     */
    public void beats(final GameInfo policy) {
        mBeats.add(policy);
    }

    /**
     * Add a policy which draws against this policy
     *
     * @param policy
     */
    public void draws(final GameInfo policy) {
        mDraws.add(policy);
    }

    /**
     * Adds a policy which wins against this policy
     *
     * @param policy
     */
    public void loses(final GameInfo policy) {
        mLoses.add(policy);
    }

    public HashSet<GameInfo> getBeats() {
        return mBeats;
    }

    public HashSet<GameInfo> getDraws() {
        return mDraws;
    }

    public HashSet<GameInfo> getLoses() {
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
        for (GameInfo p : mBeats) {
            result += p.getPlayedAgainst() + "\n";
        }
        result += "Draws:\n";
        for (GameInfo p : mDraws) {
            result += p.getPlayedAgainst() + "\n";
        }
        result += "Loses:\n";
        for (GameInfo p : mLoses) {
            result += p.getPlayedAgainst() + "\n";
        }

        return result;
    }

    @Override
    public int compareTo(final PolicyQuality o) {
        if (mBeats.containsAll(o.mBeats)) {

            // Beats more -> Better.
            if (mBeats.size() > o.mBeats.size()) {

                // Are all draw runs are in there too?
                for (GameInfo gameInfo : o.mDraws) {
                    if (!mBeats.contains(gameInfo) && !mDraws.contains(gameInfo)) {
                        return 0;
                    }
                }

                return 1;
            }

            // After here we can assume mBeats contains the same elements as o.mBeats

            if (mDraws.containsAll(o.mDraws)) {

                // Draws more -> Better.
                if (mDraws.size() > o.mDraws.size()) {
                    // More draws, including all previous ones
                    return 1;
                }

                // After this we can assume mDraws contains the same elements as o.mDraws
                // If I generally beat everything faster, then I am better.
                int mySteps = countSteps(mBeats);
                int otherSteps = countSteps(o.mBeats);
                if (mySteps < otherSteps) {
                    return 1;
                } else if (mySteps == otherSteps) {
                    return 0;
                } else {
                    return -1;
                }
            }

            if (o.mDraws.containsAll(mDraws)) {
                return -1;
            } else {
                return 0;
            }

        } else {
            // Figure out if I am worse
            if (o.mBeats.containsAll(mBeats)) {
                // Are all draw runs in there too?
                for (GameInfo gameInfo : mDraws) {
                    if (!o.mBeats.contains(gameInfo) && !o.mDraws.contains(gameInfo)) {
                        return 0;
                    }
                }

                return -1;
            }

            return 0;
        }
    }

    private static int countSteps(final Set<GameInfo> gameInfos) {
        int result = 0;
        for (GameInfo gameInfo : gameInfos) {
            result += gameInfo.getSteps();
        }

        return result;
    }
}
