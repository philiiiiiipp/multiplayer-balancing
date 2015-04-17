package nl.uva.td.ai.mcts;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.uva.td.ai.Agent;
import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.map.Field;
import nl.uva.td.game.map.GameField;
import nl.uva.td.util.Util;

public class MCTSAgent extends Agent {

    public static final double INITIAL_REWARD = 0.5;

    public static final double WIN_REWARD = 5;

    public static final double LOOSER_REWARD = 0;

    public static final double DRAW_REWARD = LOOSER_REWARD;

    private final SearchTree mSearchTree;

    /** Progressive widening threshold */
    public static final int T = 10;

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
    private final List<Integer> mActionHistoryRandomWalk = new LinkedList<Integer>();

    private final List<Integer> mEmptyTowerPositions;

    private final int mTotalAmountOfTowerFields;

    private int mFixedPolicyActionCounter = 0;

    private final List<List<Integer>> mSuccessfullActions = new ArrayList<List<Integer>>();

    public MCTSAgent(final Race race, final int totalActionAmount, final Player player) {
        super("MCTS Agent", player, race);
        mTotalActionAmount = totalActionAmount;
        mSearchTree = new SearchTree();
        mSearchTree.initialise();

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
            int fixedAction = -1;
            if (!mSuccessfullActions.isEmpty()
                    && mSuccessfullActions.get(mSuccessfullActions.size() - 1).size() > mFixedPolicyActionCounter) {
                fixedAction = mSuccessfullActions.get(mSuccessfullActions.size() - 1).get(mFixedPolicyActionCounter);
            } else {
                fixedAction = 0;
            }

            mFixedPolicyActionCounter++;
            return new Decision(fixedAction, mRace);
        }

        final TreeNode currentNode = mSearchTree.getCurrentNode();
        final int actionToTake = treeWalk();

        if (mRandomWalk != RandomWalkPhase.IN) {
            mActionHistory.add(actionToTake);
            mStateHistory.add(currentNode);
        } else {
            mActionHistoryRandomWalk.add(actionToTake);
        }

        return new Decision(actionToTake, mRace);
    }

    private int treeWalk() {

        if (mRandomWalk == RandomWalkPhase.IN || mRandomWalk == RandomWalkPhase.STARTED) {
            // Simulation
            if (mRandomWalk == RandomWalkPhase.STARTED) {
                // Tree building step 2, save the resulting state
                mSearchTree.completeTreeBuilding();
                mRandomWalk = RandomWalkPhase.IN;
            }

            return randomWalk();

        } else {
            // Selection
            TreeNode currentNode = mSearchTree.getCurrentNode();
            if (currentNode.isLeaf()) {
                return doWidening();
            } else {
                if (currentNode.getAmountOfChildren() != mTotalActionAmount
                        && currentNode.getVisitationCount() % T == 0) {
                    // Progressive widening
                    return doWidening();
                } else {
                    // normal selection
                    double actionValue = -1;
                    int mostPromissing = -1;
                    for (int action : currentNode.getListOfPerformedActions()) {
                        if (actionIsValid(action) && currentNode.getScoreOfAction(action) > actionValue) {
                            actionValue = currentNode.getScoreOfAction(action);
                            mostPromissing = action;
                        }
                    }

                    if (isTowerAction(mostPromissing)) {
                        // We want to remove the object rather than the index!
                        mEmptyTowerPositions.remove(new Integer(getTowerPlace(mostPromissing)));
                    }

                    mSearchTree.performActionOnCurrentNode(mostPromissing);
                    return mostPromissing;
                }
            }
        }
    }

    private int doWidening() {
        int desiredAction = getNextRandomAction();
        mRandomWalk = RandomWalkPhase.STARTED;
        mSearchTree.saveTreeBuildingAction(desiredAction);

        if (isTowerAction(desiredAction)) {
            // We want to remove the object rather than the index!
            mEmptyTowerPositions.remove(new Integer(getTowerPlace(desiredAction)));
        }

        return desiredAction;
    }

    private int getNextRandomAction() {
        int desiredAction = randomWalk();
        int startingAction = desiredAction;

        if (mSearchTree.getCurrentNode().wasActionVisited(desiredAction) || !actionIsValid(desiredAction)) {
            // find a non visited, valid action
            int totalAvailableActions = mTotalActionAmount;

            if (mEmptyTowerPositions.size() == 0) {
                desiredAction = startingAction = Util.RND.nextInt(4);
                totalAvailableActions = 4;
            }

            if (Util.RND.nextBoolean()) {
                // either go upwards
                for (desiredAction += 1;; desiredAction++) {
                    if (desiredAction == totalAvailableActions) {
                        desiredAction = 0;
                    }

                    if (startingAction == desiredAction
                            || !mSearchTree.getCurrentNode().wasActionVisited(desiredAction)
                            && actionIsValid(desiredAction)) {
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
                            && actionIsValid(desiredAction)) {
                        break;
                    }
                }
            }
        }

        return desiredAction;
    }

    private boolean isTowerAction(final int action) {
        return action >= 4;
    }

    private int getTowerPlace(int action) {
        if (!isTowerAction(action)) {
            // No tower placement
            return -1;
        }
        action -= 4;
        return action / mRace.getAvailableTowerAmount();
    }

    private int getTowerType(int action) {
        if (!isTowerAction(action)) {
            // No tower placement
            return -1;
        }

        action -= 4;
        return action % mTotalAmountOfTowerFields;
    }

    private boolean actionIsValid(int desiredAction) {
        if (desiredAction < 4) {
            // Update or send creep is always valid
            return true;
        }

        desiredAction -= 4;
        desiredAction /= 3;

        return mEmptyTowerPositions.contains(desiredAction);
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
    public void endInternal(final Player winner, final boolean fixed) {
        if (fixed) {
            // This is a fixed policy. So don't update, just reset
            mFixedPolicyActionCounter = 0;
            return;
        }

        double reward = 0;
        if (didIWin(winner)) {
            reward = WIN_REWARD;
        } else if (isDraw(winner)) {
            reward = DRAW_REWARD;
        } else {
            reward = LOOSER_REWARD;
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

        if (winner == mPlayer) {
            // I won
            List<Integer> successfullActionList = new ArrayList<Integer>();
            successfullActionList.addAll(mActionHistory);
            successfullActionList.addAll(mActionHistoryRandomWalk);
            mSuccessfullActions.add(successfullActionList);
        } else if (winner == Player.NONE) {
            // Draw
            draw++;
            mActionHistory.clear();
            mActionHistoryRandomWalk.clear();
        } else {
            // Lost
            mActionHistory.clear();
            mActionHistoryRandomWalk.clear();
        }

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
        mFixedPolicyActionCounter = 0;
    }

    @Override
    public void printStatistics() {
        TreeNode root = mSearchTree.getRootNode();

        System.out.println("---- Search Tree Statisctics ----");
        System.out.println("Race: " + mRace + "\t Player: " + mPlayer);
        System.out.println("Node count: " + mSearchTree.getNodeCount());
        System.out.println(" Draw: " + draw);
        System.out.print(root.nodeInfo(mRace));

    }

    @Override
    public void reset() {
        mFixedPolicyActionCounter = 0;
        mActionHistory.clear();
        mActionHistoryRandomWalk.clear();
        mSearchTree.reset();
    }
}
