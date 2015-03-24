package nl.uva.td.ai;

import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.map.GameField;

public abstract class Agent {

    protected final String mName;

    public Agent(final String name) {
        mName = name;
    }

    public abstract Decision makeDecision(final GameField yourMap, final GameField enemyMap,
            final PlayerAttributes yourAttributes, final PlayerAttributes enemyAttributes, final int elapsedSteps,
            Agent enemyAgent);

    public String getName() {
        return mName;
    }
}
