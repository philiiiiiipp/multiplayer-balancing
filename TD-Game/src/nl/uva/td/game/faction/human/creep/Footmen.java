package nl.uva.td.game.faction.human.creep;

import nl.uva.td.game.Attribute;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;

public class Footmen extends Creep {

    private final double mResistance = 0.2;

    public Footmen() {
        super(2, 0.5, 10);
    }

    @Override
    protected double modifyDamage(final double dmg, final Tower tower) {
        double resultingDmg = dmg;

        if (tower.hasAttribute(Attribute.FIRE)) {
            resultingDmg -= dmg * mResistance;
        }

        if (tower.hasAttribute(Attribute.ICE)) {
            resultingDmg += dmg * mResistance;
        }

        return resultingDmg;
    }
}
