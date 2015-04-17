package nl.uva.td.game.faction.alien.tower;

import java.util.Iterator;
import java.util.Set;

import nl.uva.td.game.faction.tower.Parasite;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;
import nl.uva.td.game.map.CreepField;

public class ParasiteTower extends Tower {

    public static final int ID = 1;

    public ParasiteTower() {
        super(false, 0, 1, 30, ID);
    }

    /**
     * Tells the tower to shot at a creep in range
     *
     * @return A list of creeps that died or null if no creep died
     */
    @Override
    public Set<Creep> shoot() {

        Iterator<CreepField> creepFieldIterator = mFieldsInRange.iterator();
        Parasite parasite = new Parasite();
        while (creepFieldIterator.hasNext()) {
            CreepField creepField = creepFieldIterator.next();

            for (Creep creep : creepField.getCreeps()) {
                if (!creep.hasMovementChange(parasite)) {
                    creep.putMovementChange(parasite);;
                    return null;
                }
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "Parasite";
    }
}
