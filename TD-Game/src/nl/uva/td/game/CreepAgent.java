package nl.uva.td.game;

import nl.uva.td.game.faction.unit.Creep;

public abstract class CreepAgent {

    public abstract Creep nextCreep(final int stepCounter);

}
