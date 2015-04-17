package nl.uva.td.game.agent;

import nl.uva.td.game.PlayerAttributes.UpgradeType;
import nl.uva.td.game.faction.Race;

public class Decision {

    public static int TOWER_FIELDS = 8;

    private static final int SEND_CREEP = 0;

    private boolean mWantsToPlaceCreep = false;

    private UpgradeType mWantsToUpgradeCreep = UpgradeType.NONE;

    private TowerPlacement mTowerPlacement = null;

    private final int mDecisionNumber;

    public Decision(int decision, final Race race) {
        mDecisionNumber = decision;

        if (decision == SEND_CREEP) {
            mWantsToPlaceCreep = true;
        } else if (decision < 4) {
            mWantsToUpgradeCreep = UpgradeType.values()[decision]; // Decision 0 == UpgradeType.NONE
        } else {
            decision -= 4;
            int towerType = decision % 3;
            int towerPosition = decision / 3;

            mTowerPlacement = new TowerPlacement(towerPosition, race.getTowerByNumber(towerType));
        }
    }

    /**
     * The towers which where decided to be placed
     *
     * @return The to be placed towers
     */
    public TowerPlacement wantsToPlaceTower() {
        return mTowerPlacement;
    }

    /**
     * The creeps which where decided to be placed
     *
     * @return The creeps to be placed
     */
    public boolean wantsToPlaceCreeps() {
        return mWantsToPlaceCreep;
    }

    /**
     * Returns the kind of upgrade the creep should get or -1 if no update was selected
     *
     * @return The kind of upgrade the creep should get or -1 if no update was selected
     */
    public UpgradeType wantsToUpdateCreep() {
        return mWantsToUpgradeCreep;
    }

    /**
     * Get the actual number of this decision
     *
     * @return The number of this decision
     */
    public int getDecisionNumber() {
        return mDecisionNumber;
    }

    @Override
    public String toString() {

        if (mWantsToPlaceCreep) {
            return "C ;";
        } else if (mWantsToUpgradeCreep != UpgradeType.NONE) {
            return mWantsToUpgradeCreep + ";";
        } else {
            return "" + mTowerPlacement.getTower() + " at " + mTowerPlacement.getTowerPosition() + ";";
        }
    }

}
