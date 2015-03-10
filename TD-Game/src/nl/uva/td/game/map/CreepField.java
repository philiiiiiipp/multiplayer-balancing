package nl.uva.td.game.map;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;

public class CreepField extends Field {

    public static final String ID = "X";

    /** The identifier for the field where creeps start walking from */
    public static final String SPECIAL_START_ID = "S";

    /** The identifier for the field where creeps are ending */
    public static final String SPECIAL_END_ID = "E";

    public enum Type {
        START,
        END,
        NONE

    }

    /** The type of creep field: START,END,NONE */
    private final Type mType;

    /** All creeps currently standing on this field */
    private final Set<Creep> mCreeps = new HashSet<Creep>();

    /** The next field on the creep way */
    private CreepField mNextField;

    /** The previous field on the creep way */
    private CreepField mPreviousField;

    public CreepField(final Type type, final int row, final int column) {
        super(Field.Type.CREEP_FIELD, row, column);

        mType = type;
    }

    /**
     * Deal damage to this field and all its standing creeps
     *
     * @param dmg
     *            The damage dealt to all the units
     * @param tower
     *            The tower which did the damage
     * @return A set of all dead creeps or null if nothing died
     */
    public Set<Creep> dealDamage(final double dmg, final Tower tower) {
        Iterator<Creep> creepIterator = mCreeps.iterator();
        Set<Creep> killedCreeps = null;

        while (creepIterator.hasNext()) {
            Creep current = creepIterator.next();

            if (current.acceptDamage(dmg, tower)) {
                // creep died

                if (killedCreeps == null) {
                    killedCreeps = new HashSet<Creep>();
                }

                killedCreeps.add(current);
                creepIterator.remove();
            }

            if (!tower.doesSplash()) return killedCreeps;
        }

        return killedCreeps;
    }

    /**
     * Adds a tower which is in range of this field and registers there as a creep field
     *
     * @param tower
     *            The tower in range
     */
    @Override
    public void addTowerInRange(final Tower tower) {
        super.addTowerInRange(tower);
        tower.registerField(this);
    }

    /**
     * Determines if there are creeps standing on this field
     *
     * @return true if there are, false if not
     */
    public boolean hasCreeps() {
        return mCreeps.size() > 0;
    }

    /**
     * Gets a creep from this field
     *
     * @return A creep currently standing on this field
     */
    public Creep getCreep() {
        return mCreeps.iterator().next();
    }

    /**
     * Get all currently on this field standing creeps
     * 
     * @return A list of all creeps on this field
     */
    public Set<Creep> getCreeps() {
        return mCreeps;

    }

    /**
     * Removes the creep from this field
     *
     * @param creep
     *            The creep to remove
     */
    public void removeCreep(final Creep creep) {
        mCreeps.remove(creep);
    }

    /**
     * Adds the creep to the list of creeps standing on this field
     *
     * @param creep
     *            The creep to add to the list
     */
    public void addCreep(final Creep creep) {
        mCreeps.add(creep);
    }

    /**
     * The next field after this one, or null if there are no more
     *
     * @return The next field or null of there are not more
     */
    public CreepField getNextField() {
        return mNextField;
    }

    /**
     * The previous field before this one, or null if there are no more
     *
     * @return The previous field
     */
    public CreepField getPreviousField() {
        return mPreviousField;
    }

    /**
     * Set the previous field of this field
     *
     * @param previousField
     */
    public void setPreviousField(final CreepField previousField) {
        mPreviousField = previousField;
    }

    /**
     * Set the next field after this one
     *
     * @param nextField
     *            The field to be appended as the next field
     */
    public void setNextField(final CreepField nextField) {
        mNextField = nextField;
    }

    /**
     * Determines if this field is the end field
     *
     * @return true if it's the final goal field, false if not
     */
    public boolean isEnd() {
        return mType == Type.END;
    }

    @Override
    public void clear() {
        super.clear();
        mCreeps.clear();
    }

    @Override
    public String toString() {
        if (mCreeps.size() == 0) {
            return "0";
        }

        return mCreeps.iterator().next().toString();
    }
}
