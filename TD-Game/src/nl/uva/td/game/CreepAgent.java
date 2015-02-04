package nl.uva.td.game;

import nl.uva.td.game.unit.Creep;
import nl.uva.td.game.unit.SimpleCreep;

public class CreepAgent {

    public Creep nextCreep(final int stepCounter) {
        return new SimpleCreep();
    }

}
