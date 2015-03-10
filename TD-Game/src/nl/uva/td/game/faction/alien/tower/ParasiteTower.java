package nl.uva.td.game.faction.alien.tower;

import java.util.Iterator;
import java.util.Set;

import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;
import nl.uva.td.game.map.CreepField;

public class ParasiteTower extends Tower {

    public ParasiteTower() {
        super(false, 0, 1, 30);
    }

    /**
     * Tells the tower to shot at a creep in range
     *
     * @return A list of creeps that died or null if no creep died
     */
    @Override
    public Set<Creep> shoot() {

        Iterator<CreepField> creepFieldIterator = mFieldsInRange.iterator();
        while (creepFieldIterator.hasNext()) {
            CreepField creepField = creepFieldIterator.next();

            for (Creep creep : creepField.getCreeps()) {
                if (!creep.hasParasite()) {
                    creep.putParasite();
                    return null;
                }
            }
        }

        return null;
    }
}
