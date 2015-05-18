package nl.uva.td.ai.mcts.actionpackage;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class ActionPackage {

    protected enum ActionPackageType {
        SEND_CREEP,
        UPDATE_CREEPS,
        BUILD_TOWER
    }

    private final ActionPackageType mType;

    public static final int MAX_ACTION_PACKAGE_LENGTH = 20;

    private final List<Integer> mActions;

    private int mActionPosition = 0;

    private final int mID;

    protected ActionPackage(final int action, final ActionPackageType type) {
        this(new LinkedList<Integer>(Arrays.asList(action)), action, type);
    }

    protected ActionPackage(final int action, final int id, final ActionPackageType type) {
        this(new LinkedList<Integer>(Arrays.asList(action)), id, type);
    }

    protected ActionPackage(final List<Integer> actions, final int id, final ActionPackageType type) {
        mActions = actions;
        mID = id;
        mType = type;
    }

    public int getID() {
        return mID;
    }

    public int getNextAction() {
        return mActions.get(mActionPosition++);
    }

    public boolean hasNextAction() {
        return mActions.size() > mActionPosition;
    }

    public boolean allowesUpdate() {
        return mType != ActionPackageType.UPDATE_CREEPS || mActions.size() == MAX_ACTION_PACKAGE_LENGTH;
    }

}
