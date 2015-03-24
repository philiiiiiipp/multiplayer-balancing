package nl.uva.td.test;

import nl.uva.td.game.CreepAgent;
import nl.uva.td.game.faction.human.creep.Footmen;
import nl.uva.td.game.faction.human.creep.Knight;
import nl.uva.td.game.faction.unit.Creep;

public class SpawnCreeps extends CreepAgent {

    int i = 0;

    @Override
    public Creep nextCreep(final int stepCounter) {
        if (stepCounter % 15 > 10 || stepCounter % 2 == 0) {
            return null;
        }

        if (i++ % 3 == 0) {
            return new Knight();
        } else {
            return new Footmen();
        }

    }
}
