package nl.uva.td.game.tower;

import nl.uva.td.game.Attribute;

public class IceTower extends Tower {

    public static final Type TYPE = Type.ICE;

    public IceTower() {
        super(false, 1, 1, 10);
        mAttributes.add(Attribute.ICE);
    }
}
