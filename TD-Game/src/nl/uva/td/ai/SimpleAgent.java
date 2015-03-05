package nl.uva.td.ai;

import java.util.ArrayList;
import java.util.List;

import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.agent.TowerPlacement;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.TowerField;
import nl.uva.td.game.tower.FireTower;
import nl.uva.td.game.tower.IceTower;
import nl.uva.td.game.tower.Tower;
import nl.uva.td.game.unit.Creep;
import nl.uva.td.game.unit.FireCreep;
import nl.uva.td.game.unit.IceCreep;
import nl.uva.td.util.Util;

/**
 * Simple agent implementation which will build towers for half the money and send creeps for the
 * other half or the rest of the tower money
 *
 * @author philipp
 *
 */
public class SimpleAgent implements Agent {

    @Override
    public Decision makeDecision(final GameField myMap, final GameField enemyMap, final PlayerAttributes myAttributes,
            final PlayerAttributes enemyAttributes, final int elapsedSteps) {
        Decision decision = new Decision();

        double myGold = myAttributes.getGold();
        double towerGold = myGold / 2;
        double creepGold = towerGold;

        boolean donePlacing = false;
        List<TowerField> freeTowerFields = new ArrayList<TowerField>(myMap.getFreeTowerFields());

        while (!donePlacing) {
            Tower tower = (Util.RND.nextBoolean() ? new FireTower() : new IceTower());

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
            Creep nextCreep = (Util.RND.nextBoolean() ? new FireCreep(3 + elapsedSteps / 10) : new IceCreep(
                    3 + elapsedSteps / 10));

            if (nextCreep.getCost() <= creepGold) {
                decision.addCreep(nextCreep);
                creepGold -= nextCreep.getCost();
            } else {
                donePlacing = true;
            }
        }

        return decision;
    }
}
