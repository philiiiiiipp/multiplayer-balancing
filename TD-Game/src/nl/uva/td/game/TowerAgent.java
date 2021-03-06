package nl.uva.td.game;

import nl.uva.td.game.tower.Tower;

public abstract class TowerAgent {

    public abstract Tower nextTower(final int stepCounter);

    public abstract int nextTowerPosition(final int stepCounter);
}
