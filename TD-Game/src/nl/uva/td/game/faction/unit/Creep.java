package nl.uva.td.game.faction.unit;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import nl.uva.td.game.faction.tower.MovementChange;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.map.CreepField;

public abstract class Creep {

    private static final int REQUIRED_MOVEMENT_FOR_A_TILE = 1;

    private double mHealth;

    private double mMaxHealth;

    private final double mCost;

    private CreepField mCurrentField;

    private final double mMovementPerTurn;

    private double mCurrentMovementCycle = 1;

    private final boolean mFlying;

    private final Set<MovementChange> mMovementChanges = new HashSet<MovementChange>();

    public Creep(final double health, final double movement, final double cost) {
        this(health, movement, cost, false);
    }

    public Creep(final double health, final double movement, final double cost, final boolean flying) {
        mHealth = mMaxHealth = health - 1;
        mCost = cost;
        mMovementPerTurn = movement;
        mFlying = flying;
    }

    /**
     * Inflicts damage on this creep
     *
     * @param dmg
     *            The damage dealt
     * @param tower
     *            The tower which dealt the damage
     * @return true if the creep died, false if not
     */
    public boolean acceptDamage(final double dmg, final Tower tower) {
        mHealth -= modifyDamage(dmg, tower);

        return mHealth <= 0;
    }

    /**
     * Removes all necessary links because this creep is dead now
     */
    public void died() {
        mCurrentField.removeCreep(this);
    }

    /**
     * Modifies the damage dealt to this creep depending on its creep type
     *
     * @param dmg
     *            The damage originally dealt to this creep
     * @param tower
     *            The tower which is dealing the damage
     * @return The true damage this creep will receive, minus resistance etc.
     */
    protected abstract double modifyDamage(final double dmg, final Tower tower);

    /**
     * Moves the creep one field forward
     *
     * @return true if the creep reached the goal field
     */
    public boolean move() {
        if (!mMovementChanges.isEmpty()) {
            Iterator<MovementChange> movementChange = mMovementChanges.iterator();
            while (movementChange.hasNext()) {
                if (movementChange.next().apply(this)) {
                    // Movement change is done
                    movementChange.remove();
                }
            }

            return false;
        }

        if ((mCurrentMovementCycle -= mMovementPerTurn) <= 0) {

            do {
                mCurrentField.removeCreep(this);

                if (mCurrentField.isEnd()) {
                    return true;
                }

                mCurrentField = mCurrentField.getNextField();
                mCurrentField.addCreep(this);

                mCurrentMovementCycle += REQUIRED_MOVEMENT_FOR_A_TILE;
            } while (mCurrentMovementCycle <= 0);

            return false;
        } else {
            return false;
        }
    }

    /**
     * Sets the field this creep is currently standing on
     *
     * @param currentField
     *            The field this creep is currently standing on
     */
    public void setCurrentField(final CreepField currentField) {
        mCurrentField = currentField;
    }

    /**
     * Gets the field this creep is currently standing on
     *
     * @return The field this creep is currently standing on
     */
    public CreepField getCurrentField() {
        return mCurrentField;
    }

    /**
     * Get the current health of this minion
     *
     * @return The current health points of this minion
     */
    public double getHealth() {
        return mHealth;
    }

    /**
     * The maximum health of this minion
     *
     * @return The maximum health of this minion
     */
    public double getMaxHealth() {
        return mMaxHealth;
    }

    /**
     * The cost of this creep
     *
     * @return The cost of this creep
     */
    public double getCost() {
        return mCost;
    }

    /**
     * Determines if this creep is a flying creep
     *
     * @return true if this creep can fly, false otherwise
     */
    public boolean isFlyign() {
        return mFlying;
    }

    /**
     * Get the current movement cycle
     *
     * @return The current movement cycle
     */
    public double getCurrentMovementCycle() {
        return mCurrentMovementCycle;
    }

    /**
     * Set the current movement cycle
     *
     * @param currentMovementCycle
     *            The desired movement cycle
     */
    public void setCurrentMovementCycle(final double currentMovementCycle) {
        mCurrentMovementCycle = currentMovementCycle;
    }

    /**
     * Get the movement this creep can do per turn
     *
     * @return The movement this creep can do per turn
     */
    public double getMovementPerTurn() {
        return mMovementPerTurn;
    }

    public boolean hasMovementChange(final MovementChange change) {
        return mMovementChanges.contains(change);
    }

    public void putMovementChange(final MovementChange change) {
        mMovementChanges.add(change);
    }

    /**
     * Get the increase in salary this creep does
     *
     * @return The increase in salary
     */
    public double getSalaryIncrease() {
        return mCost * 0.1;
    }

    @Override
    public String toString() {
        return "" + (int) mHealth;
    }
}
