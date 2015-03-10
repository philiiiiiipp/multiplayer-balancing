package nl.uva.td.experiment;

import nl.uva.td.game.TowerAgent;
import nl.uva.td.game.faction.tower.SimpleTower;
import nl.uva.td.game.faction.tower.Tower;

public class TowerPlayerAgent extends TowerAgent {

    private int mNextPosition = -1;

    @Override
    public Tower nextTower(final int stepCounter) {
        return new SimpleTower();
    }

    @Override
    public int nextTowerPosition(final int stepCounter) {
        while (mNextPosition == -1) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int returnValue = mNextPosition;
        mNextPosition = -1;
        return returnValue;
    }

    public void setNextPosition(final int position) {
        mNextPosition = position;
    }
}
