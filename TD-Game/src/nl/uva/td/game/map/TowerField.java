package nl.uva.td.game.map;

import nl.uva.td.game.faction.tower.Tower;

public class TowerField extends Field {

    public static final String ID = "O";

    private final int mGridID;

    private Tower mTower;

    public TowerField(final int row, final int column, final int gridID) {
        super(Type.TOWER_FIELD, row, column);

        mGridID = gridID;
    }

    /**
     * The currently placed tower or null if no tower is placed
     *
     * @return The tower currently placed here or null if no tower is placed
     */
    public Tower getTower() {
        return mTower;
    }

    /**
     * Set tower to be the new tower placed on this field
     *
     * @param tower
     *            The tower to be placed
     */
    public void placeTower(final Tower tower) {
        if (mTower != tower) {
            mTower = tower;
            mTower.placeOnField(this);
        }
    }

    @Override
    public void clear() {
        super.clear();
        mTower = null;
    }

    public int getGridID() {
        return mGridID;
    }

    @Override
    public String toString() {
        if (mTower != null) {
            return "X";
        } else {
            return "O";
        }
    }

    @Override
    public Score fieldValue(final Field other) {
        if (mTower == null) {
            return Score.NONE;
        }

        return Score.values()[mTower.getID() + 1];
    }
}
