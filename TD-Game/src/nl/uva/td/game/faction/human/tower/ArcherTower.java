package nl.uva.td.game.faction.human.tower;

import nl.uva.td.game.Attribute;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.tower.Tower.Type;

public class ArcherTower extends Tower {

    public static final Type TYPE = Type.FIRE;

    public ArcherTower() {
        super(false, 1, 1, 30);
        mAttributes.add(Attribute.FIRE);
    }

}
