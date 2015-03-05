package nl.uva.td.game.unit;

import nl.uva.td.game.tower.Tower;

public class SimpleCreep extends Creep {

    public SimpleCreep(final double health) {
        super(health, 10);
    }

    @Override
    protected double modifyDamage(final double dmg, final Tower tower) {
        return dmg;
    }
}
