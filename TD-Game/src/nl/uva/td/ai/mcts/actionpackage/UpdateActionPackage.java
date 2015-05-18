package nl.uva.td.ai.mcts.actionpackage;

import java.util.List;

public class UpdateActionPackage extends ActionPackage {

    public UpdateActionPackage(final List<Integer> actions, final int id) {
        super(actions, id, ActionPackageType.UPDATE_CREEPS);
    }

}
