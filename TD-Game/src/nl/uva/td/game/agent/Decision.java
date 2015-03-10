package nl.uva.td.game.agent;

import java.util.LinkedList;
import java.util.List;

import nl.uva.td.game.faction.unit.Creep;

public class Decision {

    private final List<TowerPlacement> mTowerPlacements;

    private final List<Creep> mCreepsToPlace;

    public Decision() {
        this(new LinkedList<TowerPlacement>(), new LinkedList<Creep>());
    }

    public Decision(final List<TowerPlacement> towerPlacements, final List<Creep> creepsToPlace) {
        mTowerPlacements = towerPlacements;
        mCreepsToPlace = creepsToPlace;
    }

    /**
     * Add a tower placement to this decision
     *
     * @param towerPlacement
     *            The tower placement details
     */
    public void addTowerPlacement(final TowerPlacement towerPlacement) {
        mTowerPlacements.add(towerPlacement);
    }

    /**
     * Add a creep to this decision
     * 
     * @param creep
     *            The creep to be placed next step
     */
    public void addCreep(final Creep creep) {
        mCreepsToPlace.add(creep);
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
