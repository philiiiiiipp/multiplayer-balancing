package nl.uva.td.game.faction.human.tower;

import nl.uva.td.game.Attribute;
import nl.uva.td.game.faction.tower.Tower;

public class FireTower extends Tower {

    public static final Type TYPE = Type.FIRE;

    public FireTower() {
        super(true, 1, 1, 30);
        mAttributes.add(Attribute.FIRE);
    }

}
