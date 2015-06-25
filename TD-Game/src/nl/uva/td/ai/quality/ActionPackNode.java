package nl.uva.td.ai.quality;

import java.util.HashSet;
import java.util.Set;

public class ActionPackNode {

    public final int humanAction;

    public final int alienAction;

    public final Set<ActionPackNode> children = new HashSet<>();

    public ActionPackNode(final int humanAction, final int alienAction) {
        this.humanAction = humanAction;
        this.alienAction = alienAction;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof ActionPackNode) {
            return o.hashCode() == this.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (humanAction + ";" + alienAction).hashCode();
    }

}
