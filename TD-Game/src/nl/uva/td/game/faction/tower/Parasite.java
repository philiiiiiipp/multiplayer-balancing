package nl.uva.td.game.faction.tower;

import nl.uva.td.game.faction.unit.Creep;

/**
 * This Parasite makes the creep walk backwards with its normal movement speed
 *
 * @author philipp
 *
 */
public class Parasite extends MovementChange {

    private static final int ID = 0;

    /** The duration this parasite affects the creep */
    private static final int PARASITE_DURATION = 2;

    /** The remaining duration this creep gets affected */
    private int mRemainingTurns = PARASITE_DURATION;

    /** The current movement cycle of the affected creep */
    private double mCurrentMovementCycle = 0;

    @Override
    public boolean apply(final Creep creep) {
        if (mRemainingTurns-- == 0) {
            // Add the overflowing backwards movement in case it did not move an exact number of
            // tiles
            creep.setCurrentMovementCycle(creep.getCurrentMovementCycle() + mCurrentMovementCycle);

            return true;
        }

        double movementPerTurn = creep.getMovementPerTurn();

        if ((mCurrentMovementCycle += movementPerTurn) >= 1) {

            do {
                if (creep.getCurrentField().getPreviousField() == null) {
                    // We are standing at the start
                    return false;
                }

                creep.getCurrentField().removeCreep(creep);
                creep.setCurrentField(creep.getCurrentField().getPreviousField());
                creep.getCurrentField().addCreep(creep);

            } while (--mCurrentMovementCycle >= 1);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Parasite) {
            return true;
        }

        return false;
    }
}
