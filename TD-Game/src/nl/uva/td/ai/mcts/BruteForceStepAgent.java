package nl.uva.td.ai.mcts;

import java.util.ArrayList;
import java.util.List;

import nl.uva.td.ai.Agent;
import nl.uva.td.ai.Policy;
import nl.uva.td.game.GameManager;
import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.GameResult;
import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.map.GameField;

public class BruteForceStepAgent extends Agent {

    private final Policy mPreviouslyUsedPolicy = null;

    /** The list of empty tower positions in the current game on my field **/
    private final List<Integer> mEmptyTowerPositions;

    private final int mMaxSteps = GameManager.MAX_STEPS;

    private final int mTotalActionAmount = 10;

    private final int mTotalAmountOfTowerFields = 2;

    private int mCurrentPos = 0;

    private final List<Integer> mIndices = new ArrayList<Integer>();

    public boolean mDone = false;

    public BruteForceStepAgent(final Player player, final Race race) {
        super("BruteForce", player, race);

        for (int i = 0; i < mMaxSteps; ++i) {
            mIndices.add(0);
        }

        mEmptyTowerPositions = new ArrayList<Integer>(mTotalAmountOfTowerFields);
        for (int i = 0; i < mTotalAmountOfTowerFields; ++i) {
            mEmptyTowerPositions.add(i);
        }
    }

    @Override
    protected void startInternal(final boolean fixed) {
        mDone = false;
        mCurrentPos = 0;
        mEmptyTowerPositions.clear();
        for (int i = 0; i < mTotalAmountOfTowerFields; ++i) {
            mEmptyTowerPositions.add(i);
        }
    }

    @Override
    protected Decision makeInternalDecision(final GameField yourMap, final GameField enemyMap,
            final PlayerAttributes yourAttributes, final PlayerAttributes enemyAttributes, final int elapsedSteps,
            final Agent enemyAgent, final boolean maximising) {

        int current = mIndices.get(mCurrentPos);
        if (isTowerAction(current)) {
            if (mEmptyTowerPositions.contains(new Integer(getTowerPlace(current)))) {
                // valid
                mEmptyTowerPositions.remove(new Integer(getTowerPlace(current)));

            } else {
                // invalid
                current = 0;
            }
        }

        mCurrentPos++;
        return new Decision(current, mRace);
    }

    private boolean isTowerAction(final int action) {
        return action >= 4;
    }

    private int getTowerPlace(int action) {
        if (!isTowerAction(action)) {
            // No tower placement
            return -1;
        }

        action -= 4;
        return action / mRace.getAvailableTowerAmount();
    }

    @Override
    protected void endInternal(final GameResult winner, final boolean fixed) {
        int pos = mCurrentPos - 1;
        mIndices.set(pos, mIndices.get(pos) + 1);

        while (mIndices.get(pos) == mTotalActionAmount) {
            mIndices.set(pos, 0);
            pos--;

            if (pos < 0) {
                // We are done!
                mDone = true;
                return;
            }

            mIndices.set(pos, mIndices.get(pos) + 1);
        }

        if (mCurrentPos != mIndices.size()) {
            for (int i = mCurrentPos; i < mIndices.size(); i++) {
                mIndices.set(i, 0);
            }
        }
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void printStatistics() {
        // TODO Auto-generated method stub

    }

}
