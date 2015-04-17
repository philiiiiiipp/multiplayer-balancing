package nl.uva.td.game.faction.human.tower;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.uva.td.game.Attribute;
import nl.uva.td.game.faction.tower.Frozen;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;
import nl.uva.td.game.map.CreepField;

public class IceTower extends Tower {
    public static final int ID = 2;
    public static final Type TYPE = Type.ICE;

    public IceTower() {
        super(false, 1, 1, 30, ID);
        mAttributes.add(Attribute.ICE);
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
        Frozen frozen = new Frozen();
        Creep lastCheckedCreep = null;

        // If all creeps in range are frozen we shock one again else shock an 'unfrozen' one
        L: while (creepFieldIterator.hasNext()) {
            CreepField creepField = creepFieldIterator.next();

            for (Creep creep : creepField.getCreeps()) {
                lastCheckedCreep = creep;

                if (!creep.hasMovementChange(frozen)) {
                    break L;
                }
            }
        }

        if (lastCheckedCreep != null) {
            // First freeze
            lastCheckedCreep.putMovementChange(frozen);

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
        return "Ice";
    }
}
