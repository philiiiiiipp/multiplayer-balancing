package nl.uva.td.ai.mcts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import nl.uva.td.ai.Agent;
import nl.uva.td.ai.Policy;
import nl.uva.td.ai.mcts.actionpackage.ActionPackage;
import nl.uva.td.ai.mcts.actionpackage.BuildTowerActionPackage;
import nl.uva.td.ai.mcts.actionpackage.SendCreepActionPackage;
import nl.uva.td.ai.mcts.actionpackage.UpdateActionPackage;
import nl.uva.td.game.GameManager;
import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.GameResult;
import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.map.Field;
import nl.uva.td.game.map.GameField;
import nl.uva.td.util.Util;

public class MCTSAgent extends Agent {

    public static final double INITIAL_REWARD = 0.5;

    public static final double WIN_REWARD = 5;

    public static final double LOOSER_REWARD = 0.5;

    public static final double DRAW_REWARD = LOOSER_REWARD;

    private final SearchTree mSearchTree;

    /** Progressive widening threshold */
    public static final int T = 4;

    /** The total amount of available actions */
    private final int mTotalActionAmount;

    /**
     * Defines the random walk phase
     */
    private enum RandomWalkPhase {
        OUT,
        STARTED,
        IN
    }

    /** Are we in the random walk phase **/
    private RandomWalkPhase mRandomWalk = RandomWalkPhase.OUT;

    /** The action history of the current tree walk **/
    private final List<Integer> mActionHistory = new LinkedList<Integer>();

    /** The state history of the current tree walk **/
    private final List<TreeNode> mStateHistory = new LinkedList<TreeNode>();

    /** The action history of the current random tree walk **/
    // private final List<Integer> mActionHistoryRandomWalk = new LinkedList<Integer>();

    /** The list of empty tower positions in the current game on my field **/
    private final List<Integer> mEmptyTowerPositions;

    private final int mTotalAmountOfTowerFields;

    private Policy mLastSuccessfullStrategie = null;

    private final List<List<Integer>> mUpgradeList = new ArrayList<List<Integer>>();

    private ActionPackage mCurrentActionPackage;

    private int mRunningUpgradeListPosition;

    private final HashSet<String> mUsedStrategies = new HashSet<String>();
    private String mCurrentStrategie = "";
    private long mSameStrategieCounter = 0;

    public MCTSAgent(final Race race, final int totalActionAmount, final Player player) {
        super("MCTS Agent", player, race);
        mTotalActionAmount = totalActionAmount;
        mSearchTree = new SearchTree();
        mSearchTree.initialise();

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

                    mUpgradeList.add(upgradeList);
                }
            }
        }

        mTotalAmountOfTowerFields = (totalActionAmount - 1 - race.getAvailableTowerAmount()) / 3;
        mEmptyTowerPositions = new ArrayList<Integer>(mTotalAmountOfTowerFields);
        for (int i = 0; i < mTotalAmountOfTowerFields; ++i) {
            mEmptyTowerPositions.add(i);
        }
    }

    @Override
    protected void startInternal() {

    }

    @Override
    public Decision makeInternalDecision(final GameField yourMap, final GameField enemyMap,
            final PlayerAttributes yourAttributes, final PlayerAttributes enemyAttributes, final int elapsedSteps,
            final Agent enemyAgent, final boolean fixed) {

        if (fixed) {
            if (mLastSuccessfullStrategie == null) {
                // Standard decision type is 0
                return new Decision(0, mRace);
            }

            return new Decision(mLastSuccessfullStrategie.getNextAction(), mRace);
        }
        final int actionToTake = treeWalk();

        mCurrentStrategie += actionToTake + ";";
        return new Decision(actionToTake, mRace);
    }

    private int treeWalk() {
        if (mCurrentActionPackage != null && mCurrentActionPackage.hasNextAction()) {
            return mCurrentActionPackage.getNextAction();
        }

        if (mRandomWalk == RandomWalkPhase.IN || mRandomWalk == RandomWalkPhase.STARTED) {
            // Simulation
            mRandomWalk = RandomWalkPhase.IN;
            return randomWalk();

        } else {
            // Selection
            TreeNode currentNode = mSearchTree.getCurrentNode();
            mStateHistory.add(currentNode);

            if (currentNode.isLeaf()) {

                mCurrentActionPackage = doWidening();
            } else {
                if (currentNode.getAmountOfChildren() != mTotalActionAmount - 3 + mUpgradeList.size()
                        && currentNode.getVisitationCount() % T == 0) {

                    // Progressive widening
                    mCurrentActionPackage = doWidening();

                } else {

                    // normal selection
                    double actionValue = Double.NEGATIVE_INFINITY;
                    int mostPromissing = -1;
                    for (int action : currentNode.getListOfPerformedActions()) {
                        if (actionIDIsValid(action) && currentNode.getScoreOfAction(action) > actionValue) {
                            actionValue = currentNode.getScoreOfAction(action);
                            mostPromissing = action;
                        }
                    }

                    if (isTowerActionID(mostPromissing)) {
                        // We want to remove the object rather than the index!
                        mEmptyTowerPositions.remove(new Integer(getTowerPlace(mostPromissing)));
                    }

                    mSearchTree.performActionOnCurrentNode(mostPromissing);
                    mCurrentActionPackage = getPackageForID(mostPromissing);
                }
            }

            mActionHistory.add(mCurrentActionPackage.getID());
            return mCurrentActionPackage.getNextAction();
        }
    }

    private ActionPackage getPackageForID(final int mostPromissing) {
        if (mostPromissing == 0) {
            return new SendCreepActionPackage();
        } else if (mostPromissing <= mUpgradeList.size()) {
            return new UpdateActionPackage(mUpgradeList.get(mostPromissing - 1), mostPromissing);
        } else {
            // Tower
            return new BuildTowerActionPackage(towerActionIDtoAction(mostPromissing), mostPromissing);
        }
    }

    private ActionPackage doWidening() {
        ActionPackage desiredAction = getNextRandomAction();
        mRandomWalk = RandomWalkPhase.STARTED;
        mSearchTree.completeTreeBuilding(desiredAction.getID());

        if (isTowerActionID(desiredAction.getID())) {
            // We want to remove the object rather than the index!
            mEmptyTowerPositions.remove(new Integer(getTowerPlace(desiredAction.getID())));
        }

        return desiredAction;
    }

    private ActionPackage getNextRandomAction() {
        int desiredAction = randomWalk();
        int startingAction = desiredAction;
        boolean onlyUpwards = false;

        // Account for the Upgrade simplifier
        if (desiredAction > 0) {
            // We want to upgrade
            if (desiredAction < 4) {
                if (mCurrentActionPackage == null || mCurrentActionPackage.allowesUpdate()) {
                    startingAction = desiredAction = Util.RND.nextInt(mUpgradeList.size()) + 1;
                } else {
                    onlyUpwards = true;
                    startingAction = 0;
                    desiredAction = mUpgradeList.size() + 1;
                }
            } else {
                desiredAction -= 3;
                startingAction = desiredAction += mUpgradeList.size();
            }
        }

        if (mSearchTree.getCurrentNode().wasActionVisited(desiredAction) || !actionIDIsValid(desiredAction)) {
            // find a non visited, valid action
            int totalAvailableActions = mTotalActionAmount - 3 + mUpgradeList.size();

            if (mEmptyTowerPositions.size() == 0) {
                desiredAction = startingAction = Util.RND.nextInt(mUpgradeList.size() + 1);
                totalAvailableActions = mUpgradeList.size() + 1;
            }

            if (onlyUpwards || Util.RND.nextBoolean()) {
                // either go upwards
                for (desiredAction += 1;; desiredAction++) {
                    if (desiredAction == totalAvailableActions) {
                        desiredAction = 0;
                    }

                    if (startingAction == desiredAction
                            || !mSearchTree.getCurrentNode().wasActionVisited(desiredAction)
                            && actionIDIsValid(desiredAction)) {
                        break;
                    }
                }
            } else {
                // or downwards
                for (desiredAction -= 1;; desiredAction--) {
                    if (desiredAction < 0) {
                        desiredAction = totalAvailableActions - 1;
                    }

                    if (startingAction == desiredAction
                            || !mSearchTree.getCurrentNode().wasActionVisited(desiredAction)
                            && actionIDIsValid(desiredAction)) {
                        break;
                    }
                }
            }
        }

        return getPackageForID(desiredAction);
    }

    private int towerActionIDtoAction(final int towerActionID) {
        return (towerActionID - mUpgradeList.size() + 3);
    }

    private boolean isTowerActionID(final int actionID) {
        return actionID >= mUpgradeList.size() + 1;
    }

    private int getTowerPlace(int actionID) {
        if (!isTowerActionID(actionID)) {
            // No tower placement
            return -1;
        }
        actionID -= mUpgradeList.size() + 1;
        return actionID / mRace.getAvailableTowerAmount();
    }

    private boolean actionIDIsValid(final int desiredActionID) {
        if (desiredActionID == 0) {
            return true;
        }

        if (desiredActionID < mUpgradeList.size() + 1) {
            // Not allowed to update again if previously there wasn't a maximum action package
            if (mCurrentActionPackage != null) {
                return mCurrentActionPackage.allowesUpdate();
            }

            return true;
        }

        return mEmptyTowerPositions.contains(getTowerPlace(desiredActionID));
    }

    /**
     * Get the next random walk action
     *
     * @return The next action determined by random walk
     */
    private int randomWalk() {
        int actionSpace = (mEmptyTowerPositions.size() != 0 ? 3 : 2);
        switch (Util.RND.nextInt(actionSpace)) {
        case 0:
            // Send creep
            return 0;
        case 1:
            // Update
            return Util.RND.nextInt(3) + 1;
        case 2:
            // Build tower
            int nextPosition = Util.removeRandomObject(mEmptyTowerPositions);
            nextPosition *= mRace.getAvailableTowerAmount();
            nextPosition += 4 + Util.RND.nextInt(mRace.getAvailableTowerAmount());
            return nextPosition;
        default:
            throw new RuntimeException("Invalid action space");
        }
    }

    @Override
    public void endInternal(final GameResult gameResult, final boolean fixed) {
        if (fixed) {
            // This is a fixed policy. So don't update, just reset
            if (mLastSuccessfullStrategie != null) {
                mLastSuccessfullStrategie.reset();
            }

            return;
        }

        if (mUsedStrategies.contains(mCurrentStrategie)) {
            mSameStrategieCounter++;
        } else {
            mUsedStrategies.add(mCurrentStrategie);
        }
        mCurrentStrategie = "";

        double reward = 0;
        if (didIWin(gameResult.getWinner())) {
            reward = WIN_REWARD * gameResult.getMultiplier();// GameManager.MAX_STEPS -
            // gameResult.getSteps();
        } else if (isDraw(gameResult.getWinner())) {
            reward = DRAW_REWARD;
        } else {
            reward = gameResult.getSteps() - GameManager.MAX_STEPS;
        }

        for (int historyPosition = 0; historyPosition < mStateHistory.size(); ++historyPosition) {
            final TreeNode toEvaluateNode = mStateHistory.get(historyPosition);

            final int takenAction = mActionHistory.get(historyPosition);

            final double oldReward = toEvaluateNode.getRewardForAction(takenAction);
            toEvaluateNode.setRewardForAction(takenAction, oldReward + reward);

            toEvaluateNode.increaseActionCounterFor(takenAction);
            toEvaluateNode.increaseVisitationCount();
        }

        mStateHistory.clear();
        mRandomWalk = RandomWalkPhase.OUT;

        if (gameResult.getWinner() == mPlayer) {
            // I won
            mLastSuccessfullStrategie = getLastUsedPolicy();
        } else if (gameResult.getWinner() == Player.NONE) {
            // Draw
            draw++;
        }

        mCurrentActionPackage = null;
        mActionHistory.clear();
        mSearchTree.reset();
        resetEmptyTowerFields();
    }

    private long draw = 0;

    private void resetEmptyTowerFields() {
        mEmptyTowerPositions.clear();
        for (int i = 0; i < mTotalAmountOfTowerFields; ++i) {
            mEmptyTowerPositions.add(i);
        }
    }

    public int[] generateState(final GameField yourMap, final GameField enemyMap) {
        Field[][] myField = yourMap.getGameField();
        Field[][] enemyField = yourMap.getGameField();
        int[] result = new int[myField.length * myField[0].length];

        for (int x = 0; x < myField.length; ++x) {
            for (int y = 0; y < myField[x].length; ++y) {
                result[x * myField.length + y * myField[x].length] = myField[x][y].fieldValue(enemyField[x][y])
                        .ordinal();
            }
        }

        return result;
    }

    private boolean didIWin(final Player winner) {
        return winner == mPlayer;
    }

    private boolean isDraw(final Player winner) {
        return winner == Player.NONE;
    }

    private boolean didILoose(final Player winner) {
        return !didIWin(winner) && !isDraw(winner);
    }

    @Override
    public void resetFixedPolicy() {
        if (mLastSuccessfullStrategie != null) {
            mLastSuccessfullStrategie.reset();
        }
    }

    @Override
    public void printStatistics() {
        TreeNode root = mSearchTree.getRootNode();

        System.out.println("---- Search Tree Statisctics ----");
        System.out.println("Race: " + mRace + "\t Player: " + mPlayer);
        System.out.println("Node count: " + mSearchTree.getNodeCount() + "\t Max Depth: " + mSearchTree.getMaxDepth());
        System.out.println(" Draw: " + draw + "\t Same: " + mSameStrategieCounter);
        System.out.print(root.nodeInfo(mRace, mUpgradeList));

    }

    @Override
    public void reset() {
        resetFixedPolicy();

        mCurrentActionPackage = null;
        mActionHistory.clear();
        mSearchTree.reset();
    }
}
