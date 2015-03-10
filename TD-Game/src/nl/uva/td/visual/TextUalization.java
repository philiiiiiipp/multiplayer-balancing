package nl.uva.td.visual;

import nl.uva.td.experiment.Score;
import nl.uva.td.game.GameUpdateHUB;
import nl.uva.td.game.GameUpdateSubscriber;
import nl.uva.td.game.map.Field;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.TowerField;

public class TextUalization implements GameUpdateSubscriber {

    public TextUalization() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void update(final Score score, final GameField gameField, final int subscriberID) {

        Field[][] field = gameField.getGameField();
        for (int x = 0; x < field.length; x++) {
            for (int y = 0; y < field[x].length; y++) {
                Field current = field[x][y];

                if (current instanceof TowerField) {
                    System.out.print(current);
                } else {
                    System.out.print(current);
                }
            }
            System.out.println();
        }
        System.out.println("\n\n\n\n\n\n\n");
    }

    @Override
    public void showMore(final GameUpdateHUB mGameState, final String aiName) {
        // TODO Auto-generated method stub

    }

}
