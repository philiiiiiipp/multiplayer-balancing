package nl.uva.td.game.faction.alien.tower;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.uva.td.game.faction.tower.Shock;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;
import nl.uva.td.game.map.CreepField;

public class ShockTower extends Tower {

    public static final int ID = 2;

    public ShockTower() {
        super("Shock", false, 0.5, 1, 30, ID);
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
        Creep lastCheckedCreep = null;

        // If all creeps in range are shocked we shock one again else shock an 'unshocked' one
        L: while (creepFieldIterator.hasNext()) {
            CreepField creepField = creepFieldIterator.next();

            for (Creep creep : creepField.getCreeps()) {
                lastCheckedCreep = creep;

                if (!creep.hasMovementChange(shock)) {
                    break L;
                }
            }
        }

        if (lastCheckedCreep != null) {
            // First shock
            lastCheckedCreep.putMovementChange(shock);

            // Now put damage
            if (lastCheckedCreep.acceptDamage(mDamage, this)) {
                result = new HashSet<Creep>();
                result.add(lastCheckedCreep);
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return "Shock";
    }
}
