package nl.uva.td.test;

import nl.uva.td.game.CreepAgent;
import nl.uva.td.game.unit.Creep;
import nl.uva.td.game.unit.FireCreep;
import nl.uva.td.game.unit.IceCreep;

public class SpawnCreeps extends CreepAgent {

    int i = 0;

    @Override
    public Creep nextCreep(final int stepCounter) {
        if (stepCounter % 15 > 10 || stepCounter % 2 == 0) {
            return null;
        }

        if (i++ % 3 == 0) {
            return new IceCreep(3 + stepCounter / 10);
        } else {
            return new FireCreep(3 + stepCounter / 10);
        }

    }
}
