package nl.uva.td.visual;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import nl.uva.td.ai.MCTSTacticsFinder;
import nl.uva.td.ai.quality.ActionInfo;
import nl.uva.td.ai.quality.ActionNode;
import nl.uva.td.game.GameManager;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.agent.TowerPlacement;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.faction.alien.tower.ChainLightningTower;
import nl.uva.td.game.faction.alien.tower.ParasiteTower;
import nl.uva.td.game.faction.alien.tower.ShockTower;
import nl.uva.td.game.faction.human.tower.ArcherTower;
import nl.uva.td.game.faction.human.tower.FireTower;
import nl.uva.td.game.faction.human.tower.IceTower;
import nl.uva.td.game.faction.tower.Frozen;
import nl.uva.td.game.faction.tower.Parasite;
import nl.uva.td.game.faction.tower.Shock;
import nl.uva.td.game.faction.tower.Tower;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author philipp
 */
public class AnalysisTool extends javax.swing.JFrame {

    public static List<ActionNode> mNodes;

    public static String FILENAME = "action-nodes-graph-chain-high1";

    public static double[] sTowerImpact = new double[6];

    /**
     * Creates new form AnalysingTool
     */
    public AnalysisTool() {
        initComponents();

        String text = "";
        Tower tower = new FireTower();
        text += "Fire Dmg: " + tower.getDamage() + " Range: " + tower.getRange() + " Special: Does splash\n";

        tower = new IceTower();
        text += "Ice Dmg: " + tower.getDamage() + " Range: " + tower.getRange() + " Special: "
                + Frozen.FROZEN_HINDRANCE + " for " + Frozen.FROZEN_DURATION + "\n";

        tower = new ArcherTower();
        text += "Archer Dmg: " + tower.getDamage() + " Range: " + tower.getRange() + "\n\n";

        tower = new ChainLightningTower();
        text += "Chain Dmg: " + tower.getDamage() + " Range: " + tower.getRange() + " Special: "
                + ChainLightningTower.CHAIN_JUMPS + " times, max " + ChainLightningTower.MAXIMUM_JUMP_LENGTH
                + " fields\n";

        tower = new ParasiteTower();
        text += "Parasite Dmg: " + tower.getDamage() + " Range: " + tower.getRange() + " Special: " + "Backwards for "
                + Parasite.PARASITE_DURATION + " steps\n";

        tower = new ShockTower();
        text += "Shock Dmg: " + tower.getDamage() + " Range: " + tower.getRange() + " Special: " + "Stop for "
                + Shock.SHOCK_DURATION + " steps\n";

        text += getChildTable(mNodes) + "\n";
        text += ActionNode.getActionInfo() + "\n";

        text += ActionNode.createLatexTable(ActionNode.humanInfo) + "\n";
        text += ActionNode.createLatexTable(ActionNode.alienInfo) + "\n";

        jTextArea1.setText(jTextArea1.getText() + "\n" + text);

        text += "HUMAN \n";
        text += printWithoutTower(
                new ArrayList<Tower>(Arrays.asList(new ArcherTower(), new IceTower(), new FireTower())), true, mNodes);
        text += "\nALIEN \n";

        text += printWithoutTower(
                new ArrayList<Tower>(Arrays.asList(new ChainLightningTower(), new ParasiteTower(), new ShockTower())),
                false, mNodes);

        System.out.println(text);

        PrintWriter out = null;
        try {
            out = new PrintWriter(FILENAME + "-result");
            out.println(text);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT
     * modify this code. The content of this method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        // jTextField1.setText("jTextField1");

        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(final java.awt.event.KeyEvent evt) {
                jTextField1KeyReleased(evt);
            }
        });
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1024,
                                                Short.MAX_VALUE)
                                        .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
                javax.swing.GroupLayout.Alignment.TRAILING,
                layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE,
                                javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 686, Short.MAX_VALUE)
                        .addContainerGap()));

        pack();
    }// </editor-fold>

    private void jTextField1KeyReleased(final java.awt.event.KeyEvent evt) {
        if (jTextField1.getText().length() == 0) {
            String text = getChildTable(mNodes);

            jTextArea1.setText(jTextArea1.getText() + "\n\n" + text);
            return;
        }

        if (evt.getKeyChar() != ' ') {
            return;
        }

        try {
            String arg = jTextField1.getText();
            Short nextAction = Short.parseShort(arg.substring(0, arg.indexOf(MCTSTacticsFinder.SEPERATOR)));
            String leftoverArgument = arg.substring(arg.indexOf(MCTSTacticsFinder.SEPERATOR) + 1);

            for (ActionNode node : mNodes) {
                if (node.action == nextAction) {
                    ActionNode foundNode = node;
                    if (leftoverArgument.length() != 0) {
                        foundNode = MCTSTacticsFinder.getNode(node, leftoverArgument);
                    }

                    String text = "";
                    ActionNode tmp = foundNode;
                    while (tmp != null) {
                        text += new Decision(tmp.action, Race.getRaceForType((tmp.isHuman ? Race.Type.HUMAN
                                : Race.Type.ALIEN))) + "<-";
                        tmp = tmp.parent;
                    }
                    text += "\n" + foundNode + "\n\n";

                    text += getChildTable(foundNode.mChildren);
                    jTextArea1.setText(jTextArea1.getText() + "\n\n" + text);
                    break;
                }
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static String getChildTable(final List<ActionNode> children) {
        ActionNode[] sorting = new ActionNode[GameManager.TOTAL_ACTIONS];
        for (ActionNode node : children) {
            sorting[node.action] = node;
        }

        String text = "";
        int totalWin = 0;
        int totalLose = 0;
        int totalDraw = 0;
        for (int i = 0; i < 4; ++i) {
            if (sorting[i] == null) {
                continue;
            }
            text += sorting[i].toString() + "\n";

            totalWin += sorting[i].winCounter;
            totalDraw += sorting[i].drawCounter;
            totalLose += sorting[i].loseCounter;
        }

        text += "\n";
        for (int towerType = 0; towerType < 3; ++towerType) {
            for (int towerPos = 0; towerPos < 5; ++towerPos) {
                int i = 4 + towerType + towerPos * 3;
                if (sorting[i] == null) {
                    continue;
                }
                text += sorting[i].toString() + "\n";

                totalWin += sorting[i].winCounter;
                totalDraw += sorting[i].drawCounter;
                totalLose += sorting[i].loseCounter;
            }
            text += "\n";
        }

        double total = totalWin + totalDraw + totalLose;
        final NumberFormat form = NumberFormat.getPercentInstance();
        text += "                                       Avg. Win: " + form.format(totalWin / total) + " Avg. Draw: "
                + form.format(totalDraw / total) + " Avg. Lose: " + form.format(totalLose / total) + "\n";

        return text;
    }

    public static class Result {
        public int win;
        public int draw;
        public int lose;

        public void add(final Result other) {
            this.win += other.win;
            this.draw += other.draw;
            this.lose += other.lose;
        }

        public void sub(final Result other) {
            this.win -= other.win;
            this.draw -= other.draw;
            this.lose -= other.lose;
        }

        public void add(final ActionNode other) {
            this.win += other.winCounter;
            this.draw += other.drawCounter;
            this.lose += other.loseCounter;
        }

        public void add(final ActionInfo other) {
            this.win += other.winCounter;
            this.draw += other.drawCounter;
            this.lose += other.loseCounter;
        }

        public void sub(final ActionNode other) {
            this.win -= other.winCounter;
            this.draw -= other.drawCounter;
            this.lose -= other.loseCounter;
        }
    }

    public static String printWithoutTower(final List<Tower> actions, final boolean ofFirstPlayer,
            final List<ActionNode> nodes) {
        String printout = "";

        NumberFormat form = NumberFormat.getPercentInstance();
        final DecimalFormat sDecimalFormat = new DecimalFormat("#.##");
        final DecimalFormat sLongDecimalFormat = new DecimalFormat("#.#####");

        double[] towerImpact = new double[actions.size()];
        int towerNumber = 0;

        for (Tower tower : actions) {
            printout += "Without: " + tower.getName() + "\n";
            HashMap<Byte, Result> result = new HashMap<Byte, AnalysisTool.Result>();

            for (ActionNode node : nodes) {
                without(result, tower, node, !ofFirstPlayer, false);
            }

            Race.Type type = (ofFirstPlayer ? Race.Type.HUMAN : Race.Type.ALIEN);
            ActionInfo[] info = (type == Race.Type.HUMAN ? ActionNode.humanInfo : ActionNode.alienInfo);
            for (byte i = 0; i < 4; ++i) {
                Result r = result.get(i);
                if (r == null) {
                    continue;
                }

                r.add(info[i]);
                Decision d = new Decision(i, Race.getRaceForType(type));
                double totalResult = r.win + r.draw + r.lose;
                double totalTotal = info[i].winCounter + info[i].drawCounter + info[i].loseCounter;

                double winResult = r.win / totalResult;
                double drawResult = r.draw / totalResult;
                double loseResult = r.lose / totalResult;

                double winTotal = info[i].winCounter / totalTotal;
                double drawTotal = info[i].drawCounter / totalTotal;
                double loseTotal = info[i].loseCounter / totalTotal;

                printout += d.toString() + "\t" + "Win: " + r.win + " Draw: " + r.draw + " Lose: " + r.lose + "\t"
                        + " Win: " + form.format(winResult) + " Draw: " + form.format(drawResult) + " Lose: "
                        + form.format(loseResult) + " Diff-Win: " + form.format(winResult - winTotal) + " Diff-Draw: "
                        + form.format(drawResult - drawTotal) + " Diff-Lose: " + form.format(loseResult - loseTotal)
                        + "\n";
            }
            printout += printTower(result, (byte) 4, type);
            printout += printTower(result, (byte) 5, type);
            printout += printTower(result, (byte) 6, type);

            printout += "\n\n";

            double completeImpact = 0;

            String latexTable = "\\begin{table}[H]\n\\centering\n\\begin{tabular} {| c | c | c | c | c | c | c | c |} \\hline Action & AWR & Change & ADR & Change & ALR & Change & Impact \\\\ \\hline \n";
            for (byte i = 0; i < 4; ++i) {
                Result r = result.get(i);
                if (r == null) {
                    continue;
                }

                Decision d = new Decision(i, Race.getRaceForType(type));
                double totalResult = r.win + r.draw + r.lose;
                double totalTotal = info[i].winCounter + info[i].drawCounter + info[i].loseCounter;

                double winResult = r.win / totalResult;
                double drawResult = r.draw / totalResult;
                double loseResult = r.lose / totalResult;

                double winTotal = info[i].winCounter / totalTotal;
                double drawTotal = info[i].drawCounter / totalTotal;
                double loseTotal = info[i].loseCounter / totalTotal;

                double impact = (winTotal - winResult) + 0.5 * (drawTotal - drawResult) - (loseTotal - loseResult);
                completeImpact += impact;

                latexTable += "" + d.toString() + " & " + sDecimalFormat.format(winResult) + " & "
                        + form.format(1 - winTotal / winResult) + " & " + sDecimalFormat.format(drawResult) + " & "
                        + form.format(1 - drawTotal / drawResult) + " & " + sDecimalFormat.format(loseResult) + " &"
                        + form.format(1 - loseTotal / loseResult) + " & " + sLongDecimalFormat.format(impact)
                        + "\\\\ \\hline " + (i == 3 ? "\\hline\n" : "\n");
            }

            L: for (byte towerType = 0; towerType < 3; ++towerType) {
                double avgWin = 0;
                double avgDraw = 0;
                double avgLose = 0;

                double avgTotalWin = 0;
                double avgTotalDraw = 0;
                double avgTotalLose = 0;

                for (byte towerPos = 0; towerPos < 5; ++towerPos) {
                    byte action = (byte) (4 + towerType + towerPos * 3);
                    Result r = result.get(action);
                    if (r == null) {
                        continue L;
                    }

                    avgWin += r.win;
                    avgDraw += r.draw;
                    avgLose += r.lose;

                    avgTotalWin += info[action].winCounter;
                    avgTotalDraw += info[action].drawCounter;
                    avgTotalLose += info[action].loseCounter;
                }

                final double totalAvg = avgWin + avgDraw + avgLose;
                avgWin /= totalAvg;
                avgDraw /= totalAvg;
                avgLose /= totalAvg;

                final double totalTotalAvg = avgTotalWin + avgTotalDraw + avgTotalLose;
                avgTotalWin /= totalTotalAvg;
                avgTotalDraw /= totalTotalAvg;
                avgTotalLose /= totalTotalAvg;

                double totalImpact = 0;

                for (int towerPos = 0; towerPos < 5; ++towerPos) {
                    byte action = (byte) (4 + towerType + towerPos * 3);
                    Result r = result.get(action);
                    Decision d = new Decision(action, Race.getRaceForType(type));

                    double totalResult = r.win + r.draw + r.lose;
                    double totalTotal = info[action].winCounter + info[action].drawCounter + info[action].loseCounter;

                    double winResult = r.win / totalResult;
                    double drawResult = r.draw / totalResult;
                    double loseResult = r.lose / totalResult;

                    double winTotal = info[action].winCounter / totalTotal;
                    double drawTotal = info[action].drawCounter / totalTotal;
                    double loseTotal = info[action].loseCounter / totalTotal;

                    double impact = (winTotal - winResult) + 0.5 * (drawTotal - drawResult) - (loseTotal - loseResult);
                    totalImpact += impact;
                    completeImpact += impact;

                    latexTable += "" + d.toString() + " & " + sDecimalFormat.format(winResult) + " & "
                            + form.format(1 - winTotal / winResult) + " & " + sDecimalFormat.format(drawResult) + " & "
                            + form.format(1 - drawTotal / drawResult) + " & " + sDecimalFormat.format(loseResult)
                            + " &" + form.format(1 - loseTotal / loseResult) + " & "
                            + sLongDecimalFormat.format(impact) + "\\\\ \\hline " + "\n";
                }

                latexTable += "Total & " + sDecimalFormat.format(avgWin) + " & "
                        + form.format(1 - avgTotalWin / avgWin) + " & " + sDecimalFormat.format(avgDraw) + " & "
                        + form.format(1 - avgTotalDraw / avgDraw) + " & " + sDecimalFormat.format(avgLose) + " &"
                        + form.format(1 - avgTotalLose / avgLose) + " & " + totalImpact + "\\\\ \\hline "
                        + (towerType != 3 ? "\\hline" : "") + "\n";
            }

            latexTable += "\\end{tabular}\n\\caption{Without the " + tower.getName()
                    + " Tower}\n\\label{tab:exp1_without_" + tower.getName() + "}\n\\end{table}";
            latexTable = latexTable.replace("%", "\\%");

            sTowerImpact[towerNumber + (ofFirstPlayer ? 0 : 3)] = completeImpact / 19;
            towerImpact[towerNumber++] = completeImpact / 19;

            printout += "\n" + latexTable + "\n" + sLongDecimalFormat.format(completeImpact / 19) + "\n\n";
        }

        for (int i = 0; i < actions.size(); ++i) {
            printout += actions.get(i).getName() + "\t" + sLongDecimalFormat.format(towerImpact[i]) + "\n";
        }

        return printout;
    }

    public static String printTower(final HashMap<Byte, Result> result, final byte starting, final Race.Type type) {
        String resString = "";

        ActionInfo[] info = (type == Race.Type.HUMAN ? ActionNode.humanInfo : ActionNode.alienInfo);

        for (byte i = starting; i < 19; i += 3) {
            Result r = result.get(i);
            if (r == null) {
                continue;
            }

            r.add(info[i]);
            Decision d = new Decision(i, Race.getRaceForType(type));
            double totalResult = r.win + r.draw + r.lose;
            double totalTotal = info[i].winCounter + info[i].drawCounter + info[i].loseCounter;

            double winResult = r.win / totalResult;
            double drawResult = r.draw / totalResult;
            double loseResult = r.lose / totalResult;

            double winTotal = info[i].winCounter / totalTotal;
            double drawTotal = info[i].drawCounter / totalTotal;
            double loseTotal = info[i].loseCounter / totalTotal;

            NumberFormat form = NumberFormat.getPercentInstance();
            resString += d.toString() + "\t" + "Win: " + r.win + " Draw: " + r.draw + " Lose: " + r.lose + "\t"
                    + " Win: " + form.format(winResult) + " Draw: " + form.format(drawResult) + " Lose: "
                    + form.format(loseResult) + " Diff-Win: " + form.format(winResult - winTotal) + " Diff-Draw: "
                    + form.format(drawResult - drawTotal) + " Diff-Lose: " + form.format(loseResult - loseTotal) + "\n";
        }

        return resString;
    }

    public static Result without(final HashMap<Byte, Result> result, final Tower without, final ActionNode current,
            final boolean skip, final boolean found) {
        Result r = new Result();

        if (skip) {
            for (ActionNode child : current.mChildren) {
                r.add(without(result, without, child, !skip, found));
            }

            return r;
        }

        if (found) {
            // The tower was already part of a action before this, so all has to be removed
            if (!isTower(current, without)) {
                Result my = result.get(current.action);
                if (my == null) {
                    my = new Result();
                    result.put(current.action, my);
                }

                my.sub(current);
            }
            for (ActionNode child : current.mChildren) {
                without(result, without, child, !skip, true);
            }
            return new Result();
        }

        if (isTower(current, without)) {
            // this is a "without" tower
            r.add(current);

            // We need to remove all children below too!
            for (ActionNode child : current.mChildren) {
                without(result, without, child, !skip, true);
            }

        } else {
            for (ActionNode child : current.mChildren) {
                r.add(without(result, without, child, !skip, found));
            }

            Result my = result.get(current.action);
            if (my == null) {
                my = new Result();
                result.put(current.action, my);
            }

            // my.add(current);
            my.sub(r);
        }

        return r;
    }

    private static boolean isTower(final ActionNode node, final Tower tower) {
        Decision d = new Decision(node.action, Race.getRaceForType(node.isHuman ? Race.Type.HUMAN : Race.Type.ALIEN));
        TowerPlacement t = d.wantsToPlaceTower();
        return t != null && t.getTower().isSameType(tower);
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(final String args[]) {

        mNodes = MCTSTacticsFinder.readNodes();
        new AnalysisTool().setVisible(true);
    }

    // Variables declaration - do not modify
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration
}
