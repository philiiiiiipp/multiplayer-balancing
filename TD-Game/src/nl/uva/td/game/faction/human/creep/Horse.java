package nl.uva.td.game.faction.human.creep;

import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;

public class Horse extends Creep {

    public Horse() {
        super(11, 0.8, 40);
    }

    @Override
    protected double modifyDamage(final double dmg, final Tower tower) {
        return dmg;
    }

}
