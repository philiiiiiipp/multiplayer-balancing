package nl.uva.td.ai;

import java.util.ArrayList;
import java.util.List;

import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.GameResult;
import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.alien.AlienRace;
import nl.uva.td.game.map.GameField;

/**
 * Simple agent implementation which will build towers for half the money and send creeps for the
 * other half or the rest of the tower money
 *
 * @author philipp
 *
 */
public class SimpleAlienAgent extends Agent {

    public boolean donePlacing = false;

    private final List<Integer> mActionList = new ArrayList<Integer>();

    private int mPosition = 0;

    public SimpleAlienAgent(final Player player, final String actionString) {
        super("SimpleAlienAgent", player, new AlienRace());

        String[] actions = actionString.split(";");
        for (String action : actions) {
            mActionList.add(Integer.parseInt(action));
        }
    }

    public SimpleAlienAgent(final Player player) {
        super("SimpleAlienAgent", player, new AlienRace());

        mActionList.add(10 - 6);
        mActionList.add(10 - 3);
        mActionList.add(10);
        mActionList.add(10 + 3);
        mActionList.add(11 + 6);
        mActionList.add(11 + 9);
        mActionList.add(10 + 12);
        mActionList.add(10 + 15);
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
    protected void startInternal() {
        // TODO Auto-generated method stub

    }
}
