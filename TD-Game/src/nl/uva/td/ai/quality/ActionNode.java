package nl.uva.td.ai.quality;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;

public class ActionNode {

    private static int lastUsedID = 0;

    public final int id;

    public final Race.Type raceType;

    public final int action;

    public int winCounter = 0;

    public int drawCounter = 0;

    public int loseCounter = 0;

    public final Map<Integer, ActionNode> children = new HashMap<>();

    public ActionNode(final int action, final Race.Type type) {
        this.action = action;
        this.raceType = type;
        this.id = lastUsedID++;
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof ActionNode) {
            return o.hashCode() == this.hashCode();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return action;
    }

    public void buildNodeJOutput(final PrintWriter writer) {
        String actionName = new Decision(action, Race.getRaceForType(raceType)).toString().replace("_", "");

        writer.println("CREATE (" + this.id + ":Action_" + raceType + " {name:\'" + actionName + " " + this.winCounter
                + ":" + this.drawCounter + ":" + this.loseCounter + "\'} )");

        for (ActionNode child : this.children.values()) {
            child.buildNodeJOutput(writer);
        }

        if (this.children.values().size() != 0) {
            writer.println("CREATE");

            int counter = 0;
            for (ActionNode child : this.children.values()) {
                writer.print("\t (" + this.id + ")-[:CHILD]->(" + child.id + ")");
                if (++counter == this.children.size()) {
                    writer.println();
                } else {
                    writer.println(",");
                }
            }
        }
    }
}
