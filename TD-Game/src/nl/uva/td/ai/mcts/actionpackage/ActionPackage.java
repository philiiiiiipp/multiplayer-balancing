package nl.uva.td.ai.mcts.actionpackage;

import java.util.ArrayList;
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

    public static final int MAX_ACTION_PACKAGE_LENGTH = 8;

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

    public static List<List<Integer>> generateUpdateList() {
        List<List<Integer>> updateList = new ArrayList<List<Integer>>();
        int maxLength = ActionPackage.MAX_ACTION_PACKAGE_LENGTH;
        for (int length = 1; length <= maxLength; ++length) {
            for (int a = 0; a <= length; a++) {
                for (int b = length - a; b >= 0; b--) {
                    int c = (length - a) - b;

                    List<Integer> upgradeList = new ArrayList<Integer>();
                    for (int i = 0; i < a; i++) {
                        upgradeList.add(1);
                    }
                    for (int i = 0; i < b; i++) {
                        upgradeList.add(2);
                    }
                    for (int i = 0; i < c; i++) {
                        upgradeList.add(3);
                    }

                    updateList.add(upgradeList);
                }
            }
        }

        return updateList;
    }
}
