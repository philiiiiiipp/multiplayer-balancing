package nl.uva.td.game.agent;

import nl.uva.td.game.faction.tower.Tower;

public class TowerPlacement {

    private final int mTowerPosition;

    private final Tower mTower;

    public TowerPlacement(final int position, final Tower tower) {
        mTowerPosition = position;
        mTower = tower;
    }

    /**
     * Get the desired position for this tower
     *
     * @return The desired position for this tower
     */
    public int getTowerPosition() {
        return mTowerPosition;
    }

    /**
     * The desired tower to build at this position
     *
     * @return The desired tower
     */
    public Tower getTower() {
        return mTower;
    }

}
