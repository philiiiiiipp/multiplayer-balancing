package nl.uva.td.game;

import nl.uva.td.experiment.Score;
import nl.uva.td.game.map.GameField;
import nl.uva.td.visual.Game2048;

public abstract class GameUpdateHUB extends Thread {

    private GameUpdateSubscriber mSubscriber;
    private int mSubscriberID;

    protected final GameField mGameField;

    public GameUpdateHUB(final GameField gameField, final boolean showUI) {
        mGameField = gameField;

        if (showUI) {

            while (Game2048.sInstance == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Game2048.sInstance.showMore(this);

            while (mSubscriber == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void subscriber(final GameUpdateSubscriber subscriber, final int subscriberID) {
        mSubscriber = subscriber;
        mSubscriberID = subscriberID;
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
            mSubscriber.update(score, mGameField, mSubscriberID);
        }
    }

}
