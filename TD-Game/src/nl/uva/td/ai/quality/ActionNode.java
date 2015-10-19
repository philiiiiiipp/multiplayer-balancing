package nl.uva.td.ai.quality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
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

    public static void reset() {
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

    public static String createLatexTable(final ActionInfo[] actionInfo) {
        final DecimalFormat sDecimalFormat = new DecimalFormat("#.###");
        Type type = (actionInfo[0].isHuman ? Type.HUMAN : Type.ALIEN);
        int maxTowerPos = 5;

        String result = "\\begin{table}[H]\n\\begin{tabular} {c c c c c c c} \toprule \n& AWR & ADR & ALR & Tower AWR & Tower ADR & Tower ALR\\\\ \\midrule \n";

        result += "" + new Decision(actionInfo[0].action, Race.getRaceForType(type)).toString() + " & "
                + sDecimalFormat.format(actionInfo[0].winProb()) + " & "
                + sDecimalFormat.format(actionInfo[0].drawProb()) + " & "
                + sDecimalFormat.format(actionInfo[0].loseProb()) + " & & &" + "\\\\\n";

        for (int i = 1; i < 4; ++i) {
            result += "" + new Decision(actionInfo[i].action, Race.getRaceForType(type)).toString() + " & "
                    + sDecimalFormat.format(actionInfo[i].winProb()) + " & "
                    + sDecimalFormat.format(actionInfo[i].drawProb()) + " & "
                    + sDecimalFormat.format(actionInfo[i].loseProb()) + " & & &" + "\\\\"
                    + (i == 3 ? "\\midrule\n" : "\n");
        }

        for (int towerType = 0; towerType < 3; ++towerType) {
            double avgWin = 0;
            double avgDraw = 0;
            double avgLose = 0;

            for (int towerPos = 0; towerPos < 5; ++towerPos) {
                int i = 4 + towerType + towerPos * 3;
                avgWin += actionInfo[i].winCounter;
                avgDraw += actionInfo[i].drawCounter;
                avgLose += actionInfo[i].loseCounter;
            }

            double total = avgWin + avgDraw + avgLose;
            avgWin /= total;
            avgDraw /= total;
            avgLose /= total;

            // avgWin *= 100;
            // avgDraw *= 100;
            // avgLose *= 100;

            for (int towerPos = 0; towerPos < 5; ++towerPos) {
                int i = 4 + towerType + towerPos * 3;
                result += ""
                        + new Decision(actionInfo[i].action, Race.getRaceForType(type)).toString()
                        + " & "
                        + sDecimalFormat.format(actionInfo[i].winProb())
                        + " & "
                        + sDecimalFormat.format(actionInfo[i].drawProb())
                        + " & "
                        + sDecimalFormat.format(actionInfo[i].loseProb())
                        + " & "
                        + (towerPos == 0 ? "\\multirow{" + maxTowerPos + "}{*}{" + sDecimalFormat.format(avgWin) + ""
                                + "} & \\multirow{" + maxTowerPos + "}{*}{" + sDecimalFormat.format(avgDraw) + ""
                                + "} & \\multirow{" + maxTowerPos + "}{*}{" + sDecimalFormat.format(avgLose) + ""
                                + "} " : "&&") + "\\\\"
                        + (towerPos == 4 ? (towerType == 2 ? "\\bottomrule\n" : "\\midrule\n") : "\n");
            }
        }

        return result + "\\end{tabular}\n\\caption{Text}\n\\label{tab:}\n\\end{table}";
    }

    // public static String createLatexTable(final ActionInfo[] actionInfo) {
    // final DecimalFormat sDecimalFormat = new DecimalFormat("#.###");
    // Type type = (actionInfo[0].isHuman ? Type.HUMAN : Type.ALIEN);
    // int maxTowerPos = 5;
    //
    // String result =
    // "\\begin{table}[H]\n\\begin{tabular} {| c | c | c | c | c | c | c |} \\hline & AWR & ADR & ALR & Tower AWR & Tower ADR & Tower ALR\\\\ \\hline \n";
    //
    // result += "" + new Decision(actionInfo[0].action, Race.getRaceForType(type)).toString() +
    // " & "
    // + sDecimalFormat.format(actionInfo[0].winProb()) + " & "
    // + sDecimalFormat.format(actionInfo[0].drawProb()) + " & "
    // + sDecimalFormat.format(actionInfo[0].loseProb()) + " & & &" + "\\\\ \\cline{2-4}\n";
    //
    // for (int i = 1; i < 4; ++i) {
    // result += "" + new Decision(actionInfo[i].action, Race.getRaceForType(type)).toString() +
    // " & "
    // + sDecimalFormat.format(actionInfo[i].winProb()) + " & "
    // + sDecimalFormat.format(actionInfo[i].drawProb()) + " & "
    // + sDecimalFormat.format(actionInfo[i].loseProb()) + " & & &" + "\\\\ \\cline{2-4} "
    // + (i == 3 ? "\\cline{2-7}\n" : "\n");
    // }
    //
    // for (int towerType = 0; towerType < 3; ++towerType) {
    // double avgWin = 0;
    // double avgDraw = 0;
    // double avgLose = 0;
    //
    // for (int towerPos = 0; towerPos < 5; ++towerPos) {
    // int i = 4 + towerType + towerPos * 3;
    // avgWin += actionInfo[i].winCounter;
    // avgDraw += actionInfo[i].drawCounter;
    // avgLose += actionInfo[i].loseCounter;
    // }
    //
    // double total = avgWin + avgDraw + avgLose;
    // avgWin /= total;
    // avgDraw /= total;
    // avgLose /= total;
    //
    // // avgWin *= 100;
    // // avgDraw *= 100;
    // // avgLose *= 100;
    //
    // for (int towerPos = 0; towerPos < 5; ++towerPos) {
    // int i = 4 + towerType + towerPos * 3;
    // result += ""
    // + new Decision(actionInfo[i].action, Race.getRaceForType(type)).toString()
    // + " & "
    // + sDecimalFormat.format(actionInfo[i].winProb())
    // + " & "
    // + sDecimalFormat.format(actionInfo[i].drawProb())
    // + " & "
    // + sDecimalFormat.format(actionInfo[i].loseProb())
    // + " & "
    // + (towerPos == 0 ? "\\multirow{" + maxTowerPos + "}{*}{" + sDecimalFormat.format(avgWin) + ""
    // + "} & \\multirow{" + maxTowerPos + "}{*}{" + sDecimalFormat.format(avgDraw) + ""
    // + "} & \\multirow{" + maxTowerPos + "}{*}{" + sDecimalFormat.format(avgLose) + ""
    // + "} " : "&&") + "\\\\ \\cline{2-4}" + (towerPos == 4 ? "\\cline{2-7}\n" : "\n");
    // }
    // }
    //
    // return result + "\\end{tabular}\n\\caption{Text}\n\\label{tab:}\n\\end{table}";
    // }
    // return String.format("%-7s %-10s %-10s %-10s | %-9s %-9s %-11s %-1s",
    // new Decision(this.action, Race.getRaceForType(this.raceType)) + " " + this.action, "Win: "
    // + this.winCounter, "Draw: " + this.drawCounter, "Lose: " + this.loseCounter,
    // "Win: " + form.format(this.winCounter / total), "Draw: " + form.format(this.drawCounter /
    // total),
    // "Lose: " + form.format(this.loseCounter / total), raceType);
}
