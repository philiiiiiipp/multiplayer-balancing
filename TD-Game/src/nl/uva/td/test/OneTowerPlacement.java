package nl.uva.td.test;

import nl.uva.td.game.TowerAgent;
import nl.uva.td.game.faction.tower.Tower;

public class OneTowerPlacement extends TowerAgent {

    private final Tower mType;

    private final int mPosition;

    public OneTowerPlacement(final int position, final Tower type) {
        mPosition = position;
        mType = type;
    }

    @Override
    public Tower nextTower(final int stepCounter) {
        if (stepCounter == 0) {
            return mType;
        } else {
            return null;
        }
    }

    @Override
    public int nextTowerPosition(final int stepCounter) {
        if (stepCounter == 0) {
            return mPosition;
        } else {
            return -1;
        }
    }

}
