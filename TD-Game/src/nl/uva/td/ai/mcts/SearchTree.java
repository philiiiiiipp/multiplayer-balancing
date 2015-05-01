package nl.uva.td.ai.mcts;

import java.util.List;

public class SearchTree {

    private boolean mInitialised = false;

    /** The root node of the tree **/
    private TreeNode mRootNode;

    /** The current evaluated node **/
    private TreeNode mCurrentNode;

    /** The action which got chosen in the tree building phase **/
    private int mActionForTreeBuilding;

    /** The total amount of nodes in this search tree **/
    private long mNodeCounter = 0;

    private long mCurrentDeepestDepth = 0;

    /**
     * Initialise the search tree with the root node
     *
     * @param initialState
     *            The root node
     */
    public void initialise() {
        mRootNode = mCurrentNode = new TreeNode(0);
        mNodeCounter = 0;
        mInitialised = true;
    }

    /**
     * Resets the search tree to start from the root node again
     */
    public void reset() {
        mCurrentNode = mRootNode;
    }

    /**
     * Was the search tree already initialised
     *
     * @return True if already initialised, false if not
     */
    public boolean isInitialised() {
        return mInitialised;
    }

    /**
     * Determines if the current node is a leaf node
     *
     * @return True if the current node is a leaf node
     */
    public boolean isLeafNode() {
        return mCurrentNode.isLeaf();
    }

    /**
     * Save the action used for tree building
     *
     * @param treeBuildingAction
     *            The tree building action
     */
    public void saveTreeBuildingAction(final int treeBuildingAction) {
        mActionForTreeBuilding = treeBuildingAction;
    }

    /**
     * Completes the tree building step in appending the state and action to the current tree node
     * and updating the current node to the new one
     *
     * @param currentState
     *            The resulting state from the tree building step
     */
    public void completeTreeBuilding() {
        TreeNode treeNode = new TreeNode(mCurrentNode.getDepth() + 1);

        if (treeNode.getDepth() > mCurrentDeepestDepth) {
            mCurrentDeepestDepth = treeNode.getDepth();
        }

        mCurrentNode.addChild(mActionForTreeBuilding, treeNode);
        mNodeCounter++;
        performActionOnCurrentNode(mActionForTreeBuilding);

        mActionForTreeBuilding = -1;
    }

    /**
     * Performs the given action on the current tree node and changes the current node to the
     * resulting one.
     *
     * @param action
     *            The action to perform on the current node
     */
    public void performActionOnCurrentNode(final int action) {
        mCurrentNode = mCurrentNode.getNextNodeForAction(action);
    }

    /**
     * Get all already performed actions on the current node
     *
     * @return A list of all already performed actions on the current node
     */
    public List<Integer> getPerformedActionsForCurrentNode() {
        return mCurrentNode.getListOfPerformedActions();
    }

    /**
     * Get the current active node
     *
     * @return The current active node
     */
    public TreeNode getCurrentNode() {
        return mCurrentNode;
    }

    /**
     * Get the total amount of nodes in this tree
     *
     * @return The total amount of nodes
     */
    public long getNodeCount() {
        return mNodeCounter;
    }

    /**
     * Get the root node
     *
     * @return The root node
     */
    public TreeNode getRootNode() {
        return mRootNode;
    }

    /**
     * Resets the search tree to an empty uninitialised one
     */
    public void clear() {
        mRootNode = mCurrentNode = null;
        mActionForTreeBuilding = -1;
        mInitialised = false;
    }

    /**
     * Get the current deepest depth this search tree has nodes
     *
     * @return The deepest depth of this search tree
     */
    public long getMaxDepth() {
        return mCurrentDeepestDepth;
    }

    @Override
    public String toString() {
        return mNodeCounter + "";
    }
}
