package nl.uva.td.test;

import nl.uva.td.game.CreepAgent;
import nl.uva.td.game.unit.Creep;
import nl.uva.td.game.unit.SimpleCreep;

public class SpawnSimpleCreeps extends CreepAgent {

    @Override
    public Creep nextCreep(final int stepCounter) {
        if (stepCounter % 15 > 10 || stepCounter % 2 == 0) {
            return null;
        }

        return new SimpleCreep(3 + stepCounter / 15);
    }
}
