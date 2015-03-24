package nl.uva.td.game.faction.alien.creep;

import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;

public class Minion extends Creep {

    public Minion() {
        super(1, 0.3, 6);
    }

    @Override
    protected double modifyDamage(final double dmg, final Tower tower) {
        return dmg;
    }

}
