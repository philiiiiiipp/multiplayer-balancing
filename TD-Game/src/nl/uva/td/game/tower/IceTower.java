package nl.uva.td.game.tower;

import nl.uva.td.game.Attribute;

public class IceTower extends Tower {

    public IceTower() {
        super(false, 1, 1);
        mAttributes.add(Attribute.ICE);
    }
}
