package nl.uva.td.game.faction.tower;

import nl.uva.td.game.faction.unit.Creep;

public abstract class MovementChange {

    public abstract boolean apply(final Creep creep);

}
