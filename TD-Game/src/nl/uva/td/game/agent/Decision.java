package nl.uva.td.game.agent;

import java.util.List;

import nl.uva.td.game.unit.Creep;

public class Decision {

    private final List<TowerPlacement> mTowerPlacements;

    private final List<Creep> mCreepsToPlace;

    public Decision(final List<TowerPlacement> towerPlacements, final List<Creep> creepsToPlace) {
        mTowerPlacements = towerPlacements;
        mCreepsToPlace = creepsToPlace;
    }

    /**
     * The towers which where decided to be placed
     * 
     * @return The to be placed towers
     */
    public List<TowerPlacement> wantsToPlaceTowers() {
        return mTowerPlacements;
    }

    /**
     * The creeps which where decided to be placed
     * 
     * @return The creeps to be placed
     */
    public List<Creep> wantsToPlaceCreeps() {
        return mCreepsToPlace;
    }

}
