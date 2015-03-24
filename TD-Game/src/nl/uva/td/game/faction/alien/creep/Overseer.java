package nl.uva.td.game.faction.alien.creep;

import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;

public class Overseer extends Creep {

    public Overseer() {
        super(9, 0.2, 10);
    }

    @Override
    protected double modifyDamage(final double dmg, final Tower tower) {
        return dmg;
    }

}
