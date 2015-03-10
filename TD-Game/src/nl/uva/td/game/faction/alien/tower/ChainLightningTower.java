package nl.uva.td.game.faction.alien.tower;

import java.util.HashSet;
import java.util.Set;

import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;
import nl.uva.td.game.map.CreepField;

public class ChainLightningTower extends Tower {

    /** The maximum amount of jumps the lightning bolt does */
    private final static int CHAIN_JUMPS = 4;

    /** The maximum amount of fields which can be between two creeps */
    private final static int MAXIMUM_JUMP_LENGTH = 3;

    public ChainLightningTower() {
        super(false, 1, 2, 10);
    }

    /**
     * Tells the tower to shot at a creep in range
     *
     * @return A list of creeps that died or null or an empty list if no creep died
     */
    @Override
    public Set<Creep> shoot() {
        Set<Creep> killedCreeps = new HashSet<Creep>();

        if (mLockedOnCreep != null
                && (mLockedOnCreep.getHealth() <= 0 || !mFieldsInRange.contains(mLockedOnCreep.getCurrentField()))) {
            // Creep walked out of range or is dead
            mLockedOnCreep = null;
        }

        if (mLockedOnCreep == null) {
            // find a new creep
            mLockedOnCreep = findCreepInRange();
        }

        if (mLockedOnCreep != null) {
            // Fire on that creep
            if (mLockedOnCreep.acceptDamage(mDamage, this)) {
                killedCreeps.add(mLockedOnCreep);
                mLockedOnCreep.died();
            }

            return lightningJump(mLockedOnCreep.getCurrentField(), killedCreeps, CHAIN_JUMPS);

        } else {
            // No creep in sight
            return null;
        }
    }

    private Set<Creep> lightningJump(final CreepField currentField, final Set<Creep> killedCreeps,
            final int remainingJumps) {

        if (remainingJumps == 0) {
            return killedCreeps;
        }

        CreepField jumpedToField = currentField;
        for (int forward = 0; forward < MAXIMUM_JUMP_LENGTH; forward++) {
            jumpedToField = jumpedToField.getNextField();

            if (jumpedToField == null) {
                // no next field
                break;
            }

            if (jumpedToField.hasCreeps()) {
                Creep shootingAtCreep = jumpedToField.getCreep();
                if (shootingAtCreep.acceptDamage(mDamage, this)) {
                    shootingAtCreep.died();
                    killedCreeps.add(shootingAtCreep);
                }

                return lightningJump(jumpedToField, killedCreeps, remainingJumps - 1);
            }
        }

        jumpedToField = currentField;
        for (int backWard = 0; backWard < MAXIMUM_JUMP_LENGTH; backWard++) {
            jumpedToField = jumpedToField.getPreviousField();

            if (jumpedToField == null) {
                // no previous field
                break;
            }

            if (jumpedToField.hasCreeps()) {
                Creep shootingAtCreep = jumpedToField.getCreep();
                if (shootingAtCreep.acceptDamage(mDamage, this)) {
                    shootingAtCreep.died();
                    killedCreeps.add(shootingAtCreep);
                }

                return lightningJump(jumpedToField, killedCreeps, remainingJumps - 1);
            }
        }

        return killedCreeps;
    }
}