package nl.uva.td.game.faction.alien.creep;

import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;

public class Dragon extends Creep {

    public Dragon() {
        super(15, 0.8, 55);
    }

    @Override
    protected double modifyDamage(final double dmg, final Tower tower) {
        return dmg;
    }

}
