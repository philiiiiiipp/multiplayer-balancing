package nl.uva.td.game.map;

import java.util.ArrayList;
import java.util.List;

import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.faction.unit.Creep;

public class GameField {

    /** The field every creep starts on */
    private final CreepField mStartField;

    /** The field every creep wants to reach */
    private final CreepField mEndField;

    /** All possible tower placement fields */
    private final List<TowerField> mTowerFields;

    /** A list of all currently free tower fields */
    private final List<TowerField> mFreeTowerFields;

    /** All creep walking fields, including start and end */
    private final List<CreepField> mCreepFields;

    /** The whole game field, including all fields at its exact position */
    private final Field[][] mGameField;

    public GameField(final CreepField startField, final CreepField endField, final List<TowerField> towerFields,
            final List<CreepField> creepFields, final Field[][] gameField) {

        mStartField = startField;
        mEndField = endField;
        mTowerFields = towerFields;
        mFreeTowerFields = new ArrayList<TowerField>(mTowerFields);
        mCreepFields = creepFields;
        mGameField = gameField;
    }

    public GameField(final GameField other) {

        mStartField = other.mStartField;
        mEndField = other.mEndField;
        mTowerFields = new ArrayList<TowerField>(other.mTowerFields);
        mFreeTowerFields = new ArrayList<TowerField>(other.mFreeTowerFields);
        mCreepFields = other.mCreepFields;
        mGameField = other.mGameField;
    }

    public void addCreepToTheGame(final Creep creep) {
        creep.setCurrentField(mStartField);
        mStartField.addCreep(creep);
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
        if (mTowerFields.get(nextTowerPosition).getTower() == null) {
            mTowerFields.get(nextTowerPosition).placeTower(nextTower);

            mFreeTowerFields.remove(mTowerFields.get(nextTowerPosition));

            return true;
        }

        return false;
    }

    public List<TowerField> getTowerFields() {
        return mTowerFields;
    }

    public List<TowerField> getFreeTowerFields() {
        return mFreeTowerFields;
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
