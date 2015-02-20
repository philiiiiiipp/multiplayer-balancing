package nl.uva.td.game.unit;

import java.util.LinkedList;
import java.util.List;

import nl.uva.td.game.map.CreepField;
import nl.uva.td.game.tower.Tower;

public abstract class Creep {

    private double mHealth;

    private double mMaxHealth;

    private CreepField mCurrentField;

    private final List<Tower> mRegisteredTowers = new LinkedList<Tower>();

    public Creep(final double health) {
        mHealth = mMaxHealth = health - 1;
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
     * Modifies the damage dealt to this creep depending on its creep type
     * 
     * @param dmg
     *            The damage originally dealt to this creep
     * @param tower
     *            The tower which is dealing the damage
     * @return The true damage this creep will receive, minus resitance etc.
     */
    protected abstract double modifyDamage(final double dmg, final Tower tower);

    /**
     * Moves the creep one field forward
     *
     * @return true if the creep reached the goal field
     */
    public boolean move() {
        mCurrentField.removeCreep(this);

        if (mCurrentField.isEnd()) {
            return true;
        }

        mCurrentField = mCurrentField.getNextField();
        mCurrentField.addCreep(this);

        return false;
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
     * @return The field this creep is currenlty standing on
     */
    public CreepField getCurrentField() {
        return mCurrentField;
    }

    /**
     * Get the current health of this minion
     *
     * @return The current health points of this minion
     */
    public int getHealth() {
        return (int) mHealth;
    }

    /**
     * The maximum health of this minion
     *
     * @return The maximum health of this minion
     */
    public double getMaxHealth() {
        return mMaxHealth;
    }

    @Override
    public String toString() {
        return "" + (int) mHealth;
    }
}
