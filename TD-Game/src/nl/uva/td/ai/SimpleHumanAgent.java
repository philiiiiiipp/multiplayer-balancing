package nl.uva.td.ai;

import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.faction.human.HumanRace;
import nl.uva.td.game.map.GameField;

/**
 * Simple agent implementation which will build towers for half the money and send creeps for the
 * other half or the rest of the tower money
 *
 * @author philipp
 *
 */
public class SimpleHumanAgent extends Agent {

    private final Race mRace = new HumanRace();

    public SimpleHumanAgent(final Player player) {
        super("SimpleElementalAgent", player, new HumanRace());
    }

    @Override
    public Decision makeInternalDecision(final GameField myMap, final GameField enemyMap,
            final PlayerAttributes myAttributes, final PlayerAttributes enemyAttributes, final int elapsedSteps,
            final Agent enemyAgentfinal, final boolean maximising) {

        return null;
    }

    @Override
    public void endInternal(final Player winner, final boolean fixed) {
        // TODO Auto-generated method stub

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
