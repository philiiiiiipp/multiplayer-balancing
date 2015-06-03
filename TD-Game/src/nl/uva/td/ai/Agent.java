package nl.uva.td.ai;

import java.util.LinkedList;
import java.util.List;

import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.GameResult;
import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.map.GameField;

public abstract class Agent {

    protected final Player mPlayer;

    protected final String mName;

    protected final Race mRace;

    protected final List<Decision> mLastDecisionChain = new LinkedList<Decision>();

    public Agent(final String name, final Player player, final Race race) {
        mName = name;
        mPlayer = player;
        mRace = race;
    }

    public final void start(final boolean fixed) {
        mLastDecisionChain.clear();
        startInternal(fixed);
    }

    protected abstract void startInternal(final boolean fixed);

    public final Decision makeDecision(final GameField yourMap, final GameField enemyMap,
            final PlayerAttributes yourAttributes, final PlayerAttributes enemyAttributes, final int elapsedSteps,
            final Agent enemyAgent, final boolean maximising) {

        Decision decision = makeInternalDecision(yourMap, enemyMap, yourAttributes, enemyAttributes, elapsedSteps,
                enemyAgent, maximising);

        mLastDecisionChain.add(decision);
        return decision;
    }

    protected abstract Decision makeInternalDecision(final GameField yourMap, final GameField enemyMap,
            final PlayerAttributes yourAttributes, final PlayerAttributes enemyAttributes, final int elapsedSteps,
            final Agent enemyAgent, final boolean maximising);

    public final void end(final GameResult winner, final boolean fixed) {
        endInternal(winner, fixed);
    }

    protected abstract void endInternal(GameResult winner, boolean fixed);

    public boolean hasMoreFixedStrategies() {
        return false;
    }

    public abstract void reset();

    public abstract void printStatistics();

    public String getName() {
        return mName;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public Race getRace() {
        return mRace;
    }

    public List<Decision> getLastDecisionChain() {
        return mLastDecisionChain;
    }

    public Policy getLastUsedPolicy() {
        return new Policy(mRace, mLastDecisionChain);
    }

    public void resetFixedPolicy() {}
}
