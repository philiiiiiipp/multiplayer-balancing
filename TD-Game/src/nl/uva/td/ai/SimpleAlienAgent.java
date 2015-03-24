package nl.uva.td.ai;

import java.util.ArrayList;
import java.util.List;

import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.agent.TowerPlacement;
import nl.uva.td.game.faction.alien.creep.Dragon;
import nl.uva.td.game.faction.alien.creep.Minion;
import nl.uva.td.game.faction.alien.creep.Overseer;
import nl.uva.td.game.faction.human.tower.IceTower;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.TowerField;
import nl.uva.td.util.Util;

/**
 * Simple agent implementation which will build towers for half the money and send creeps for the
 * other half or the rest of the tower money
 *
 * @author philipp
 *
 */
public class SimpleAlienAgent extends Agent {

    public SimpleAlienAgent() {
        super("SimpleAlienAgent");
    }

    public boolean donePlacing = false;

    @Override
    public Decision makeDecision(final GameField myMap, final GameField enemyMap, final PlayerAttributes myAttributes,
            final PlayerAttributes enemyAttributes, final int elapsedSteps, final Agent enemyAgent) {
        Decision decision = new Decision();

        double myGold = myAttributes.getGold();
        double towerGold = myGold / 2;
        double creepGold = towerGold;

        List<TowerField> freeTowerFields = new ArrayList<TowerField>(myMap.getFreeTowerFields());

        while (!donePlacing) {
            Tower tower = new IceTower();

            if (tower.getCost() <= towerGold && myMap.getFreeTowerFields().size() != 0) {
                TowerField nextTowerField = Util.removeRandomObject(freeTowerFields);
                decision.addTowerPlacement(new TowerPlacement(nextTowerField.getGridID(), tower));

                towerGold -= tower.getCost();
            } else {
                donePlacing = true;
            }
        }

        creepGold += towerGold;
        donePlacing = false;
        while (!donePlacing) {
            Creep nextCreep = null;

            switch (Util.RND.nextInt(3)) {
            case 0:
                nextCreep = new Minion();
                break;
            case 1:
                nextCreep = new Overseer();
                break;
            case 2:
                nextCreep = new Dragon();
                break;
            }

            if (nextCreep.getCost() <= creepGold) {
                decision.addCreep(nextCreep);
                creepGold -= nextCreep.getCost();
            } else {
                nextCreep = new Minion();

                if (nextCreep.getCost() <= creepGold) {
                    decision.addCreep(nextCreep);
                    creepGold -= nextCreep.getCost();
                } else {
                    donePlacing = true;
                }
            }
        }

        return decision;
    }
}
