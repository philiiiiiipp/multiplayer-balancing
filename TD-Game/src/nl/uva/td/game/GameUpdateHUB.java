package nl.uva.td.game;

import nl.uva.td.experiment.Score;
import nl.uva.td.game.map.GameField;

public abstract class GameUpdateHUB extends Thread {

    private GameUpdateSubscriber mSubscriber;

    protected final GameField mGameField;

    public GameUpdateHUB(final GameField gameField, final boolean showUI) {
        mGameField = gameField;
    }

    public void subscriber(final GameUpdateSubscriber subscriber) {
        mSubscriber = subscriber;
    }

    public void unsubscribe(final GameUpdateSubscriber subscriber) {
        if (mSubscriber == subscriber) {
            mSubscriber = null;
        } else {
            throw new RuntimeException("Trying to unsubscribe without beeing subscribed");
        }
    }

    public GameField getGameField() {
        return mGameField;
    }

    public void updateUI(final Score score) {
        if (mSubscriber != null) {
            mSubscriber.update(score);
        }
    }

}
