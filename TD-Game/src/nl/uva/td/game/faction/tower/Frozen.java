package nl.uva.td.game.faction.tower;

import nl.uva.td.game.faction.unit.Creep;

/**
 * The frozen status hinders the movement of this creep by 90%
 *
 * @author philipp
 *
 */
public class Frozen extends MovementChange {

    private static final int ID = 0;

    /** The duration this parasite affects the creep */
    private static final int FROZEN_DURATION = 2;

    /** The percentage of hindrance the freezing does */
    private static final double FROZEN_HINDRANCE = 0.9;

    /** The remaining duration this creep gets affected */
    private int mRemainingTurns = FROZEN_DURATION;

    @Override
    public boolean apply(final Creep creep) {
        if (mRemainingTurns-- == 0) {
            return true;
        }

        double currentMovementCycle = creep.getCurrentMovementCycle();

        if ((currentMovementCycle -= creep.getMovementPerTurn() * FROZEN_HINDRANCE) <= 0) {

            do {
                creep.getCurrentField().removeCreep(creep);

                if (creep.getCurrentField().isEnd()) {
                    return true;
                }

                creep.setCurrentField(creep.getCurrentField().getNextField());
                creep.getCurrentField().addCreep(creep);

                currentMovementCycle += Creep.REQUIRED_MOVEMENT_FOR_A_TILE;
                creep.setCurrentMovementCycle(currentMovementCycle);
            } while (currentMovementCycle <= 0);

            return false;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ID;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Frozen) {
            return true;
        }

        return false;
    }
}
