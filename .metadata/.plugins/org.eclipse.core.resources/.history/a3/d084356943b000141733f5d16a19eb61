package nl.uva.td.game.map;

import java.util.List;

import nl.uva.td.game.tower.Tower;
import nl.uva.td.game.unit.Creep;

public class GameField {

    /** The field every creep starts on */
    private final CreepField mStartField;

    /** The field every creep wants to reach */
    private final CreepField mEndField;

    /** All possible tower placement fields */
    private final List<TowerField> mTowerFields;

    /** All creep walking fields, including start and end */
    private final List<CreepField> mCreepFields;

    /** The whole game field, including all fields at its exact position */
    private final Field[][] mGameField;

    public GameField(final CreepField startField, final CreepField endField, final List<TowerField> towerFields,
            final List<CreepField> creepFields, final Field[][] gameField) {

        mStartField = startField;
        mEndField = endField;
        mTowerFields = towerFields;
        mCreepFields = creepFields;
        mGameField = gameField;
    }

    public void addCreepToTheGame(final Creep creep) {
        creep.setCurrentField(mStartField);
    }

    /**
     * Adds a given tower to the field at a given position. If there is already a tower, the tower
     * is not added.
     * 
     * @param nextTower
     *            The tower to be added
     * @param nextTowerPosition
     *            The position the tower should be added
     * @return True if the tower was successfully added, false if not.
     */
    public boolean addTowerToTheGame(final Tower nextTower, final int nextTowerPosition) {
        if (mTowerFields.get(nextTowerPosition).getTower() != null) {
            mTowerFields.get(nextTowerPosition).placeTower(nextTower);
            return true;
        }

        return false;
    }

    public List<TowerField> getTowerFields() {
        return mTowerFields;
    }

    public List<CreepField> getCreepFields() {
        return mCreepFields;
    }

    public Field[][] getGameField() {
        return mGameField;
    }

    public void clear() {
        for (int x = 0; x < mGameField.length; ++x) {
            for (int y = 0; y < mGameField[x].length; ++y) {
                mGameField[x][y].clear();
            }
        }
    }

    public void print() {
        for (Field[] row : mGameField) {
            for (Field field : row) {
                System.out.print(field);
            }
            System.out.println();
        }
    }

}
