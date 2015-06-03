package nl.uva.td.ai;

import java.util.ArrayList;
import java.util.List;

import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.GameResult;
import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.map.GameField;

public class SimpleAgent extends Agent {

    private final List<Integer> mActionList = new ArrayList<Integer>();

    private int mPosition = 0;

    public SimpleAgent(final Player player, final Race race, final String actionString) {
        super("SimpleAgent " + player + " " + race.getName(), player, race);

        String[] actions = actionString.split(";");
        for (String action : actions) {
            mActionList.add(Integer.parseInt(action));
        }
    }

    public SimpleAgent(final Player player, final Race race) {
        super("SimpleAgent " + player + " " + race.getName(), player, race);

        mActionList.add(26);
        for (int i = 0; i < 55; ++i) {
            mActionList.add(1);
        }
        for (int i = 0; i < 18; ++i) {
            mActionList.add(0);
        }
        for (int i = 0; i < 90; ++i) {
            mActionList.add(0);
        }
    }

    @Override
    public Decision makeInternalDecision(final GameField myMap, final GameField enemyMap,
            final PlayerAttributes myAttributes, final PlayerAttributes enemyAttributes, final int elapsedSteps,
            final Agent enemyAgent, final boolean maximising) {

        if (mPosition == mActionList.size()) {
            return new Decision(0, mRace);
        } else {
            return new Decision(mActionList.get(mPosition++), mRace);
        }

    }

    @Override
    public void endInternal(final GameResult winner, final boolean fixed) {
        mPosition = 0;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub

    }

    @Override
    public void printStatistics() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void startInternal(final boolean fixed) {
        // TODO Auto-generated method stub

    }
}
