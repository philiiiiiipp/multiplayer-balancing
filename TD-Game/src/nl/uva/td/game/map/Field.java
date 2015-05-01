package nl.uva.td.game.map;

import java.util.HashSet;
import java.util.Set;

import nl.uva.td.game.faction.tower.Tower;

/** Represents a field inside the game **/
public abstract class Field {

    /** The score of field **/
    public enum Score {
        NONE,
        LOWER,
        EQUALS,
        HIGHER
    }

    /** The type of field **/
    public enum Type {
        CREEP_FIELD,
        TOWER_FIELD
    }

    /** All towers which have this field in their range */
    private final Set<Tower> mTowersInRange = new HashSet<Tower>();

    /** The Field to the north */
    protected Field mNorth;

    /** The field to the east */
    protected Field mEast;

    /** The field to the south */
    protected Field mSouth;

    /** The field to the west */
    protected Field mWest;

    /** The type of the field */
    protected final Type mType;

    /** The row number of the field */
    protected final int mRow;

    /** The column number of the field */
    protected final int mColumn;

    public Field(final Field other) {
        mType = other.mType;
        mRow = other.mRow;
        mColumn = other.mColumn;
    }

    public Field(final Type type, final int row, final int column) {
        mType = type;
        mRow = row;
        mColumn = column;
    }

    /**
     * Adds a tower which is in range of this field
     *
     * @param tower
     *            The tower in range
     */
    public void addTowerInRange(final Tower tower) {
        mTowersInRange.add(tower);
    }

    /**
     * Get all towers in range of this field
     *
     * @return The towers in range of this field
     */
    public Set<Tower> getTowersInRange() {
        return mTowersInRange;
    }

    /**
     * Get the field to the north of this field
     *
     * @return The field to the north
     */
    public Field getNorth() {
        return mNorth;
    }

    /**
     * Set the field to the north of this field
     *
     * @param north
     */
    public void setNorth(final Field north) {
        mNorth = north;
    }

    /**
     * Get the field to the east of this field
     *
     * @return The field to the east
     */
    public Field getEast() {
        return mEast;
    }

    /**
     * Set the field to the east of this field
     *
     * @param east
     */
    public void setEast(final Field east) {
        mEast = east;
    }

    /**
     * Get the field to the south of this field
     *
     * @return The field to the south
     */
    public Field getSouth() {
        return mSouth;
    }

    /**
     * Set the field to the south of this field
     *
     * @param south
     */
    public void setSouth(final Field south) {
        mSouth = south;
    }

    /**
     * Get the field to the west of this field
     *
     * @return The field to the west
     */
    public Field getWest() {
        return mWest;
    }

    /**
     * Set the field to the west of this field
     *
     * @param west
     */
    public void setWest(final Field west) {
        mWest = west;
    }

    /**
     * The row number of this field in the map grid
     *
     * @return The row number
     */
    public int getRow() {
        return mRow;
    }

    /**
     * The column number of this field in the map grid
     *
     * @return The column number
     */
    public int getColumn() {
        return mColumn;
    }

    public void clear() {
        mTowersInRange.clear();
    }

    /**
     * The type of the field
     *
     * @return its type
     */
    public Type getType() {
        return mType;
    }

    public abstract Score fieldValue(Field other);

    @Override
    public String toString() {
        return "X: " + mColumn + " Y: " + mRow;
    }
}
