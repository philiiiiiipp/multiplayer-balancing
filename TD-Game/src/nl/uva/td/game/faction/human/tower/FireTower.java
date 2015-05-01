package nl.uva.td.game.faction.human.tower;

import nl.uva.td.game.Attribute;
import nl.uva.td.game.faction.tower.Tower;

public class FireTower extends Tower {

    public static final int ID = 1;
    public static final Type TYPE = Type.FIRE;

    public FireTower() {
        super(true, 0.9, 1, 30, ID);
        mAttributes.add(Attribute.FIRE);
    }

    @Override
    public String toString() {
        return "Fire";
    }

}
