package nl.uva.td.game;

import nl.uva.td.experiment.Score;
import nl.uva.td.game.map.GameField;

public interface GameUpdateSubscriber {

    void showMore(final GameUpdateHUB mGameState, final String aiName);

    void update(Score score, GameField gameField, int subscriberID);
}
