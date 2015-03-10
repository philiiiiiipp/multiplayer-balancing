package nl.uva.td.game.faction.tower;

import nl.uva.td.game.faction.unit.Creep;

public class Parasite extends MovementChange {

    private final int PARASITE_LENGTH = 2;

    private int mRemainingTurns = PARASITE_LENGTH;

    private double mCurrentMovementCycle;

    public Parasite(final Creep creep) {
        mCurrentMovementCycle = creep.getCurrentMovementCycle();
    }

    @Override
    public boolean apply(final Creep creep) {
        if (mRemainingTurns-- == 0) {
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
                creep.getCurrentField().getPreviousField().addCreep(creep);

            } while (--mCurrentMovementCycle >= 1);
        }

        creep.setCurrentMovementCycle(mCurrentMovementCycle);
        return false;
    }
}
