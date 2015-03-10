package nl.uva.td.game.faction.human.creep;

import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;

public class Horse extends Creep {

    public Horse(double health, double movement, double cost) {
        super(health, movement, cost);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected double modifyDamage(double dmg, Tower tower) {
        // TODO Auto-generated method stub
        return 0;
    }

}
