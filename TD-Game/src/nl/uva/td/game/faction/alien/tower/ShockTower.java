package nl.uva.td.game.faction.alien.tower;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.uva.td.game.faction.tower.Shock;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;
import nl.uva.td.game.map.CreepField;

public class ShockTower extends Tower {

    public ShockTower() {
        super(false, 0.2, 1, 30);
    }

    /**
     * Tells the tower to shot at a creep in range
     *
     * @return A list of creeps that died or null if no creep died
     */
    @Override
    public Set<Creep> shoot() {
        Set<Creep> result = null;

        Iterator<CreepField> creepFieldIterator = mFieldsInRange.iterator();
        Shock shock = new Shock();
        while (creepFieldIterator.hasNext()) {
            CreepField creepField = creepFieldIterator.next();

            for (Creep creep : creepField.getCreeps()) {
                if (!creep.hasMovementChange(shock)) {
                    // First shock
                    creep.putMovementChange(shock);

                    // Now put damage
                    if (creep.acceptDamage(mDamage, this)) {
                        result = new HashSet<Creep>();
                        result.add(creep);
                    }

                    return result;
                }
            }
        }

        return result;
    }
}
