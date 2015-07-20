package nl.uva.td.ai.quality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.LinkedList;

import nl.uva.td.game.GameManager;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.faction.Race.Type;

public class ActionNode {

    public final static String END_OF_CHILDREN = "X";

    public final boolean isHuman;

    public final byte action;

    public final ActionNode parent;

    public int winCounter = 0;

    public int drawCounter = 0;

    public int loseCounter = 0;

    public static final ActionInfo[] humanInfo = new ActionInfo[GameManager.TOTAL_ACTIONS];

    public static final ActionInfo[] alienInfo = new ActionInfo[GameManager.TOTAL_ACTIONS];

    public final LinkedList<ActionNode> mChildren = new LinkedList<ActionNode>();

    static {
        for (int i = 0; i < GameManager.TOTAL_ACTIONS; ++i) {
            humanInfo[i] = new ActionInfo(i, Race.Type.HUMAN);
            alienInfo[i] = new ActionInfo(i, Race.Type.ALIEN);
        }
    }

    public ActionNode(final short action, final Race.Type type, final int winCounter, final int drawCounter,
            final int loseCounter, final ActionNode parent, final int depth) {
        this(action, type, parent);

        this.winCounter = winCounter;
        this.drawCounter = drawCounter;
        this.loseCounter = loseCounter;

        ActionInfo info = null;
        if (isHuman) {
            info = humanInfo[this.action];

        } else {
            info = alienInfo[this.action];
        }

        info.score += (winCounter / depth) + (drawCounter * 0.5 / depth) - (loseCounter / depth);

        info.winCounter += winCounter;
        info.drawCounter += drawCounter;
        info.loseCounter += loseCounter;
    }

    public ActionNode(final short action, final Race.Type type, final ActionNode parent) {
        this.action = (byte) action;
        this.isHuman = type == Type.HUMAN;
        this.parent = parent;
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

    public boolean buildNodeJOutput(final PrintWriter writer) {
        // String actionName = new Decision(action,
        // Race.getRaceForType(raceType)).toString().replace("_", "");

        // writer.println("CREATE (a" + this.id + ":Action_" + raceType + " {name:\'" + actionName +
        // " " + this.winCounter
        // + ":" + this.drawCounter + ":" + this.loseCounter + "\'} )");

        // boolean childrenCreated = false;
        // int childPosition = 0;
        // for (ActionNode child : this.children) {
        // if (child == null) {
        // continue;
        // }
        // childrenCreated = child.buildNodeJOutput(writer);
        //
        // if (++childPosition != this.children.values().size() && childrenCreated) {
        // writer.println();
        // }
        // }
        //
        // if (childrenCreated) {
        // if (this.children.size() != 0) {
        // writer.println(",");
        // } else {
        // writer.println();
        // }
        // }
        //
        // if (this.children.values().size() != 0) {
        // if (!childrenCreated) {
        // writer.println("CREATE");
        // }
        //
        // int counter = 0;
        // for (ActionNode child : this.children.values()) {
        // writer.print("\t (a" + this.id + ")-[:CHILD]->(a" + child.id + ")");
        // if (++counter != this.children.size()) {
        // writer.println(",");
        // }
        // }
        // }

        return true; // childrenCreated || this.children.values().size() != 0;
    }

    public void saveGraph(final PrintWriter writer) {
        writer.println(action + ";" + (isHuman ? 0 : 1) + ";" + winCounter + ";" + drawCounter + ";" + loseCounter);

        for (ActionNode child : mChildren) {

            child.saveGraph(writer);
        }
        writer.println(END_OF_CHILDREN);
    }

    public static void readGraph(final BufferedReader reader, final ActionNode parent, final int depth)
            throws IOException {
        String currentLine;
        while (!(currentLine = reader.readLine()).equals(END_OF_CHILDREN)) {
            String[] arguments = currentLine.split(";");

            short action = Short.parseShort(arguments[0]);
            Race.Type type = Integer.parseInt(arguments[1]) == Race.Type.HUMAN.ordinal() ? Race.Type.HUMAN
                    : Race.Type.ALIEN;

            int winCounter = Integer.parseInt(arguments[2]);
            int drawCounter = Integer.parseInt(arguments[3]);
            int loseCounter = Integer.parseInt(arguments[4]);
            ActionNode nextNode = new ActionNode(action, type, winCounter, drawCounter, loseCounter, parent, depth);

            if (parent != null) {
                parent.mChildren.add(nextNode);
            }

            readGraph(reader, nextNode, depth + 1);
        }
    }

    @Override
    public String toString() {
        double total = this.drawCounter + this.winCounter + this.loseCounter;

        NumberFormat form = NumberFormat.getPercentInstance();
        int norm = 13;
        int norm2 = 10;
        return f(new Decision(this.action, Race.getRaceForType(getType())).toString(), 4) + " " + this.action + " \t"
                + f("Win: " + this.winCounter, norm) + f(" Draw: " + this.drawCounter, norm)
                + f(" Lose: " + this.loseCounter, norm) + f(" \tWin: " + form.format(this.winCounter / total), norm2)
                + f(" Draw: " + form.format(this.drawCounter / total), norm2)
                + f(" Lose: " + form.format(this.loseCounter / total), norm2) + "   \t" + getType();
    }

    private String f(final String s, final int preferedLength) {
        String result = s;
        while (result.length() < preferedLength) {
            result += " ";
        }

        return result;
    }

    public Race.Type getType() {
        if (this.isHuman) {
            return Race.Type.HUMAN;
        } else {
            return Race.Type.ALIEN;
        }
    }

    public ActionNode getChild(final int action) {
        for (ActionNode child : mChildren) {
            if (child.action == action) {
                return child;
            }
        }

        return null;
    }

    public static String getActionInfo() {
        String result = "---- HUMAN ----\n";
        result += getActionInfo(humanInfo);

        result += "---- ALIEN ----\n";
        result += getActionInfo(alienInfo);

        return result;
    }

    private static String getActionInfo(final ActionInfo[] actionInfo) {
        String result = "";

        for (int i = 0; i < 4; ++i) {
            result += actionInfo[i].toString() + "\n";
        }

        result += "\n";
        for (int towerType = 0; towerType < 3; ++towerType) {
            for (int towerPos = 0; towerPos < 5; ++towerPos) {
                result += actionInfo[4 + towerType + towerPos * 3].toString() + "\n";
            }
            result += "\n";
        }

        return result;
    }

    // return String.format("%-7s %-10s %-10s %-10s | %-9s %-9s %-11s %-1s",
    // new Decision(this.action, Race.getRaceForType(this.raceType)) + " " + this.action, "Win: "
    // + this.winCounter, "Draw: " + this.drawCounter, "Lose: " + this.loseCounter,
    // "Win: " + form.format(this.winCounter / total), "Draw: " + form.format(this.drawCounter /
    // total),
    // "Lose: " + form.format(this.loseCounter / total), raceType);
}
