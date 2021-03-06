package nl.uva.td.test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nl.uva.td.game.GameManager;
import nl.uva.td.game.map.Field;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.game.map.TowerField;
import nl.uva.td.game.tower.Tower;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestGame {

    /**
     * O decodes a tower-placement field X is a way field where the creeps walk S is the start field
     * for the creeps E is the end field for the creeps
     */
    private static final String TEST_FIELD_1 = "OOOSO\n" + "OXXXO\n" + "OXOOO\n" + "OXXXO\n" + "OOOEO";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {}

    @Before
    public void setUp() throws Exception {}

    /**
     * Determines if the tower ranges get calculated correctly
     */
    @Test
    public void towerRangeTest() {
        GameField gameField = Parser.parse(TEST_FIELD_1);

        List<Integer> positionsList = new LinkedList<Integer>(Arrays.asList(0, 3, 5, 7, 12, 15));
        List<Tower> towerList = ListTowerPlacement.generateSimpleTowerList(positionsList);

        GameManager gameManager = new GameManager(new SpawnCreeps(), new ListTowerPlacement(towerList,
                positionsList), gameField, false);
        gameManager.run();

        int[][] expected = { { 1, 1, 0, 2, 2 }, { 1, 2, 1, 3, 2 }, { 0, 1, 1, 2, 1 }, { 1, 2, 1, 2, 1 },
                { 1, 1, 0, 1, 1 } };

        for (int x = 0; x < gameField.getGameField().length; x++) {
            for (int y = 0; y < gameField.getGameField()[x].length; ++y) {
                Field currentField = gameField.getGameField()[x][y];
                Assert.assertEquals(expected[x][y], currentField.getTowersInRange().size());
            }
        }
    }

    /**
     * Prints the amount of towers which can shoot at each of the fields
     *
     * @param gameField
     *            The game field to test
     */
    private void printRanges(final GameField gameField) {
        for (int x = 0; x < gameField.getGameField().length; x++) {
            for (int y = 0; y < gameField.getGameField()[x].length; ++y) {
                Field currentField = gameField.getGameField()[x][y];
                if (currentField.getTowersInRange().size() != 0) {
                    System.out.print(currentField.getTowersInRange().size());
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }

    /**
     * Prints the amount of towers which can shoot at each of the fields
     *
     * @param gameField
     *            The game field to test
     */
    private void printTowers(final GameField gameField) {
        for (int x = 0; x < gameField.getGameField().length; x++) {
            for (int y = 0; y < gameField.getGameField()[x].length; ++y) {
                Field currentField = gameField.getGameField()[x][y];
                if (currentField instanceof TowerField) {
                    if (((TowerField) currentField).getTower() != null) {
                        System.out.print("X");
                    } else {
                        System.out.print(".");
                    }
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }
}
