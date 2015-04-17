package nl.uva.td.game.faction.alien;

import nl.uva.td.game.faction.Race;
import nl.uva.td.game.faction.alien.tower.ChainLightningTower;
import nl.uva.td.game.faction.alien.tower.ParasiteTower;
import nl.uva.td.game.faction.alien.tower.ShockTower;
import nl.uva.td.game.faction.tower.Tower;

public class AlienRace implements Race {

    @Override
    public Tower getTowerByNumber(final int number) {
        switch (number) {
        case ChainLightningTower.ID:
            return new ChainLightningTower();
        case ParasiteTower.ID:
            return new ParasiteTower();
        case ShockTower.ID:
            return new ShockTower();
        default:
            throw new RuntimeException("Wrong tower ID");
        }

    }

    @Override
    public String getName() {
        return "Alien";
    }

    @Override
    public int getAvailableTowerAmount() {
        return 3;
    }

}
