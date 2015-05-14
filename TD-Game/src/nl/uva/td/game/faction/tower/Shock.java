package nl.uva.td.game.faction.tower;

import nl.uva.td.game.faction.unit.Creep;

/**
 * This movement change shocks the creep so it stands at its place for a given time
 *
 * @author philipp
 *
 */
public class Shock extends MovementChange {

    private static final int ID = 0;

    /** The duration this parasite affects the creep */
    private static final int PARASITE_DURATION = 2;

    /** The remaining duration this creep gets affected */
    private int mRemainingTurns = PARASITE_DURATION;

    @Override
    public boolean apply(final Creep creep) {
        if (mRemainingTurns-- == 0) {
            // Add the overflowing backwards movement in case it did not move an exact number of
            // tiles
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Shock) {
            return true;
        }

        return false;
    }
}