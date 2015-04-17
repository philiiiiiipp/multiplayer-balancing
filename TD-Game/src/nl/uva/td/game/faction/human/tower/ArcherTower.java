package nl.uva.td.game.faction.human.tower;

import nl.uva.td.game.faction.tower.Tower;

public class ArcherTower extends Tower {

    public static final int ID = 0;

    public ArcherTower() {
        super(false, 1, 3, 30, ID);
    }

    @Override
    public String toString() {
        return "Archer";
    }
}
