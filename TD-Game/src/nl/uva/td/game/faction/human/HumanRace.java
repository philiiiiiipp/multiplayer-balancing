package nl.uva.td.game.faction.human;

import nl.uva.td.game.faction.Race;
import nl.uva.td.game.faction.human.tower.ArcherTower;
import nl.uva.td.game.faction.human.tower.FireTower;
import nl.uva.td.game.faction.human.tower.IceTower;
import nl.uva.td.game.faction.tower.Tower;

public class HumanRace implements Race {

    @Override
    public Tower getTowerByNumber(final int number) {
        switch (number) {
        case ArcherTower.ID:
            return new ArcherTower();
        case FireTower.ID:
            return new FireTower();
        case IceTower.ID:
            return new IceTower();
        default:
            throw new RuntimeException("Wrong tower ID");
        }

    }

    @Override
    public String getName() {
        return "Human";
    }

    @Override
    public int getAvailableTowerAmount() {
        return 3;
    }
}
