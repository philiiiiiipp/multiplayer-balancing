package nl.uva.td.game.faction.human.creep;

import nl.uva.td.game.Attribute;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;

public class Knight extends Creep {

    private final double mResistance = 0.2;

    public Knight(final double health) {
        super(health, 1, 10);
    }

    @Override
    protected double modifyDamage(final double dmg, final Tower tower) {
        double resultingDmg = dmg;

        if (tower.hasAttribute(Attribute.ICE)) {
            resultingDmg -= dmg * mResistance;
        }

        if (tower.hasAttribute(Attribute.FIRE)) {
            resultingDmg += dmg * mResistance;
        }

        return resultingDmg;
    }

}
