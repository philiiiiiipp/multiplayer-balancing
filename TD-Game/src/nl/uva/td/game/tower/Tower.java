package nl.uva.td.game.tower;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.uva.td.game.Attribute;
import nl.uva.td.game.map.CreepField;
import nl.uva.td.game.map.Field;
import nl.uva.td.game.map.TowerField;
import nl.uva.td.game.unit.Creep;

public abstract class Tower {

    public enum Type {
        FIRE,
        ICE
    }

    /** A list of all creeps in range of this tower */
    protected Set<CreepField> mFieldsInRange = new HashSet<CreepField>();

    protected Creep mLockedOnCreep;

    /** If this tower deals splash damage */
    protected final boolean mSplash;

    /** The damage this tower deals per step */
    protected final double mDamage;

    /** The field this tower is standing on */
    protected TowerField mField;

    /** The range of this tower */
    protected final int mRange;

    /** The cost of to build this tower */
    protected final double mCost;

    /** The attributes of this tower, e.g. ICE / FIRE ... */
    protected final Set<Attribute> mAttributes = new HashSet<Attribute>();

    public Tower(final boolean splash, final double damage, final int range, final double cost) {
        mSplash = splash;
        mDamage = damage;
        mRange = range;
        mCost = cost;
    }

    /**
     * Tells the tower to shot at a creep in range
     *
     * @return A list of creeps that died or null if no creep died
     */
    public Set<Creep> shoot() {

        if (mLockedOnCreep != null
                && (mLockedOnCreep.getHealth() <= 0 || !mFieldsInRange.contains(mLockedOnCreep.getCurrentField()))) {
            // Creep walked out of range or is dead
            mLockedOnCreep = null;
        }

        if (mLockedOnCreep == null) {
            // find a new creep
            Iterator<CreepField> creepFieldIterator = mFieldsInRange.iterator();
            while (creepFieldIterator.hasNext() && mLockedOnCreep == null) {
                CreepField creepField = creepFieldIterator.next();

                if (creepField.hasCreeps()) {
                    mLockedOnCreep = creepField.getCreep();
                    break;
                }
            }
        }

        if (mLockedOnCreep != null) {
            // Fire on that creep
            return mLockedOnCreep.getCurrentField().dealDamage(mDamage, this);
        } else {
            // No creep in sight
            return null;
        }
    }

    /**
     * Register a field which is in range of this tower
     *
     * @param inRange
     *            The field which is in range
     */
    public void registerField(final CreepField inRange) {
        mFieldsInRange.add(inRange);
    }

    /**
     * Creates a bidirectional relation between tower and field.
     *
     * @param towerField
     *            The field this tower is standing on
     */
    public void placeOnField(final TowerField towerField) {
        if (mField != towerField) {
            mField = towerField;
            mField.placeTower(this);

            Field currentField = mField;
            currentField.addTowerInRange(this);
            int overflowNorth = 0, overflowSouth = 0, overflowWest = 0, overflowEast = 0;

            for (int range = 0; range < mRange; range++) {
                if (currentField.getNorth() != null) {
                    currentField = currentField.getNorth();
                    currentField.addTowerInRange(this);

                } else {
                    overflowNorth += 1;
                }

                for (int west = 0; west < 1 + 2 * range - overflowEast; ++west) {
                    if (currentField.getWest() != null) {
                        currentField = currentField.getWest();
                        currentField.addTowerInRange(this);
                    } else {
                        overflowWest = 1 + 2 * range - west;
                        break;
                    }
                }
                overflowEast = 0;

                for (int south = 0; south < 2 + 2 * range - overflowNorth; ++south) {
                    if (currentField.getSouth() != null) {
                        currentField = currentField.getSouth();
                        currentField.addTowerInRange(this);

                    } else {
                        overflowSouth = 2 + 2 * range - south;
                        break;
                    }
                }
                overflowNorth = 0;

                for (int east = 0; east < 2 + 2 * range - overflowWest; ++east) {
                    if (currentField.getEast() != null) {
                        currentField = currentField.getEast();

                        currentField.addTowerInRange(this);

                    } else {
                        overflowEast = 2 + 2 * range - east;
                        break;
                    }
                }
                overflowWest = 0;

                for (int north = 0; north < 2 + 2 * range - overflowSouth; ++north) {
                    if (currentField.getNorth() != null) {
                        currentField = currentField.getNorth();
                        currentField.addTowerInRange(this);

                    } else {
                        overflowNorth = 2 + 2 * range - north;
                        break;
                    }
                }
                overflowSouth = 0;
            }
        }
    }

    /**
     * Does this tower do splash damage?
     *
     * @return true if it does splash damage, false if not
     */
    public boolean doesSplash() {
        return mSplash;
    }

    /**
     * Determines if this tower has a certain attribute
     *
     * @param attribute
     *            The attribute to ask for
     * @return true if it contains this attribute, false if not
     */
    public boolean hasAttribute(final Attribute attribute) {
        return mAttributes.contains(attribute);
    }

    /**
     * Get the cost to build this tower
     *
     * @return The cost to build this tower
     */
    public double getCost() {
        return mCost;
    }
}
