package nl.uva.td.ai;

import java.util.LinkedList;
import java.util.List;

import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;

public class Policy {

    private final Race mRace;

    private final List<Integer> mPolicy;

    private int mCurrentPolicyPosition = 0;

    public Policy(final Race race, final List<Decision> decisions) {
        mPolicy = new LinkedList<Integer>();
        mRace = race;

        for (Decision decision : decisions) {
            mPolicy.add(decision.getDecisionNumber());
        }
    }

    public Policy(final List<Integer> policy, final Race race) {
        mPolicy = policy;
        mRace = race;
    }

    public int getNextAction() {
        if (mCurrentPolicyPosition < mPolicy.size()) {
            return mPolicy.get(mCurrentPolicyPosition++);
        }

        return 0;
    }

    public List<Integer> getActionList() {
        return mPolicy;
    }

    public Race getRace() {
        return mRace;
    }

    public void reset() {
        mCurrentPolicyPosition = 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Policy) {
            return this.toString().equals(o.toString());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        String result = "";

        for (int action : mPolicy) {
            result += new Decision(action, mRace);
        }

        return result + mRace.getName();
    }
}
