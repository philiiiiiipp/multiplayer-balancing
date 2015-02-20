package nl.uva.td.game.tower;

import nl.uva.td.game.Attribute;

public class FireTower extends Tower {

    public FireTower() {
        super(false, 1, 1);
        mAttributes.add(Attribute.FIRE);
    }

}
