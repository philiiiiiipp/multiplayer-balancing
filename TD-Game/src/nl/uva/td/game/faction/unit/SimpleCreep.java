package nl.uva.td.game.faction.unit;

import nl.uva.td.game.faction.tower.Tower;

public class SimpleCreep extends Creep {

    public SimpleCreep(final double health) {
        super(health, 1, 10, false);
    }

    @Override
    protected double modifyDamage(final double dmg, final Tower tower) {
        return dmg;
    }
}
