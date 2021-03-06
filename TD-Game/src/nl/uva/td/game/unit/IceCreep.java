package nl.uva.td.game.unit;

import nl.uva.td.game.Attribute;
import nl.uva.td.game.tower.Tower;

public class IceCreep extends Creep {

    private final double mResistance = 0.2;

    public IceCreep(final double health) {
        super(health);
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
