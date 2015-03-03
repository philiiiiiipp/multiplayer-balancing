package nl.uva.td.game.tower;

import nl.uva.td.game.Attribute;

public class FireTower extends Tower {

    public static final Type TYPE = Type.FIRE;

    public FireTower() {
        super(false, 1, 1, 10);
        mAttributes.add(Attribute.FIRE);
    }

}
