package nl.uva.td.game;

import nl.uva.td.experiment.Score;

public interface GameUpdateSubscriber {

    public void update(final Score score);

}