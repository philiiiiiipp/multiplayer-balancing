package nl.uva.td.game.unit;

import java.util.LinkedList;
import java.util.List;

import nl.uva.td.game.map.CreepField;
import nl.uva.td.game.tower.Tower;

public abstract class Creep {

    private double mHealth;

    private CreepField mCurrentField;

    private final List<Tower> mRegisteredTowers = new LinkedList<Tower>();

    public Creep(final double health) {
        mHealth = health;
    }

    /**
     * Inflicts damage on this creep
     *
     * @param dmg
     *            The damage dealt
     * @return true if the creep died, false if not
     */
    public boolean acceptDamage(final double dmg) {
        mHealth -= dmg;

        return mHealth <= 0;
    }

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

    @Override
    public String toString() {
        return "" + (int) mHealth;
    }
}