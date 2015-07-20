package nl.uva.td.ai.mcts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;

public class TreeNode {

    /** Tuning constant for the exploration bonus */
    public static final double C = 3 * Math.sqrt(2);

    /** The reward given this state and an action **/
    private final HashMap<Integer, Double> mActionReward = new HashMap<>();

    /** The number of times a given action was taken in this state **/
    private final HashMap<Integer, Integer> mActionCounter = new HashMap<>();

    /** The resulting tree nodes given this state and an action **/
    private final HashMap<Integer, TreeNode> mChildrens = new HashMap<>();

    /** The visitation count n_i of this tree node **/
    private int mVisitationCout = 0;

    /** The depth in the tree **/
    private final int mDepth;

    public TreeNode(final int depth) {
        mDepth = depth;
    }

    /**
     * Add a child node
     *
     * @param action
     *            The action resulting in that child
     * @param treeNode
     *            The node resulting from that action
     */
    public void addChild(final int action, final TreeNode treeNode) {
        if (mChildrens.containsKey(action)) throw new RuntimeException("NONO");
        mChildrens.put(action, treeNode);
    }

    /**
     * Evaluates if this node is a leaf
     *
     * @return True if it is a leaf, false if not
     */
    public boolean isLeaf() {
        return mChildrens.isEmpty();
    }

    /**
     * Get the resulting tree node for the specific action
     *
     * @param action
     *            The action to take in this node
     * @return The resulting tree node
     */
    public TreeNode getNextNodeForAction(final int action) {
        return mChildrens.get(action);
    }

    /**
     * Generate a list of all already performed actions on this node
     *
     * @return A list of all already performed actions on this node
     */
    public List<Integer> getListOfPerformedActions() {
        return new ArrayList<Integer>(mChildrens.keySet());
    }

    /**
     * Increases the visitation counter by 1
     */
    public void increaseVisitationCount() {
        mVisitationCout++;
    }

    /**
     * Get the visitation count of this tree node
     *
     * @return The visitation count of this tree node
     */
    public int getVisitationCount() {
        return mVisitationCout;
    }

    /**
     * Get the amount of children under this node
     *
     * @return The amount of children of this node
     */
    public int getAmountOfChildren() {
        return mChildrens.size();
    }

    /**
     * Determines if the given action was already visited once in this node
     *
     * @param action
     *            The given action
     * @return True if visited, false if not
     */
    public boolean wasActionVisited(final int action) {
        return mChildrens.containsKey(action);
    }

    /**
     * Create a string containing all info of this node
     *
     * @param race
     *            The race this node belongs to
     * @return All available information to this node
     */
    public String nodeInfo(final Race race, final List<List<Integer>> upgradeList) {
        String result = "";
        for (Integer action : mActionReward.keySet()) {
            if (action == 0) {
                Decision decision = new Decision(action, race);
                result += decision + "\t" + getScoreOfAction(action) + "\n";
            } else if (action > upgradeList.size()) {
                Decision decision = new Decision(action - upgradeList.size() + 3, race);
                result += decision + "\t" + getScoreOfAction(action) + "\n";
            } else {
                // List<Integer> upgrades = upgradeList.get(action - 1);
                // for (Integer upgrade : upgrades) {
                // Decision decision = new Decision(upgrade, race);
                // result += decision;
                // }
                // result += "\t" + getScoreOfAction(action) + "\n";
            }

        }

        return result;
    }

    /**
     * Increases the action counter for the specific action by 1
     *
     * @param action
     *            The given action to increase the counter for
     */
    public void increaseActionCounterFor(final int action) {
        Integer actionCounter = mActionCounter.get(action);
        if (actionCounter == null) {
            actionCounter = 0;
        }

        mActionCounter.put(action, ++actionCounter);
    }

    /**
     * Get the reward for a given action
     *
     * @param takenAction
     *            The action taken
     * @return The reward for the given action
     */
    public double getRewardForAction(final int takenAction) {
        if (!mActionReward.containsKey(takenAction)) {
            return MCTSAgent.INITIAL_REWARD;
        } else {
            return mActionReward.get(takenAction);
        }
    }

    /**
     * Get the number of times the given action was taken in this node
     *
     * @param takenAction
     *            The taken action
     * @return The number of times this action was taken
     */
    public int getNumOfTimesActionWasTaken(final int takenAction) {
        if (!mActionCounter.containsKey(takenAction)) {
            return 0;
        }

        return mActionCounter.get(takenAction);
    }

    public double getScoreOfAction(final int action) {
        return getRewardForAction(action) / mActionCounter.get(action) + getExplorationBonus(action);
    }

    public double getExplorationBonus(final int action) {
        return C * Math.sqrt(Math.log(mVisitationCout) / mActionCounter.get(action));
    }

    /**
     * Set the reward for taking a specific action in this state
     *
     * @param action
     *            The taken action
     * @param newReward
     *            The resulting reward
     */
    public void setRewardForAction(final int action, final double reward) {
        mActionReward.put(action, reward);
    }

    /**
     * The depth in the tree counted from 0
     *
     * @return The depth in the tree
     */
    public int getDepth() {
        return mDepth;
    }
}