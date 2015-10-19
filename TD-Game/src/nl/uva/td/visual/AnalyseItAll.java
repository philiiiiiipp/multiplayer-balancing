package nl.uva.td.visual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.uva.td.ai.MCTSTacticsFinder;
import nl.uva.td.ai.quality.ActionNode;
import nl.uva.td.game.faction.alien.tower.ChainLightningTower;
import nl.uva.td.game.faction.alien.tower.ParasiteTower;
import nl.uva.td.game.faction.alien.tower.ShockTower;
import nl.uva.td.game.faction.human.tower.ArcherTower;
import nl.uva.td.game.faction.human.tower.FireTower;
import nl.uva.td.game.faction.human.tower.IceTower;
import nl.uva.td.game.faction.tower.Tower;

public class AnalyseItAll {

    static int done = 0;

    public static void main(final String[] args) {
        File folder = new File("/Users/philipp/Documents/ms/TD-Game/Results");

        for (final File fileEntry : folder.listFiles()) {
            analyseFile(fileEntry);
        }
    }

    public static void analyseFile(final File file) {
        if (file.getName().startsWith(".")) {
            return;
        }

        if (file.isDirectory()) {
            for (final File fileEntry : file.listFiles()) {
                analyseFile(fileEntry);
            }

            return;
        }

        if (!file.getName().endsWith("result.txt") && !file.getName().contains("move")) {
            System.out.println("Analysing " + file.getName());

            // File to analyse
            List<ActionNode> mNodes = MCTSTacticsFinder.readNodes(file.getPath());

            String text = AnalysisTool.getChildTable(mNodes) + "\n";
            text += ActionNode.getActionInfo() + "\n";

            text += ActionNode.createLatexTable(ActionNode.humanInfo) + "\n\n";
            text += ActionNode.createLatexTable(ActionNode.alienInfo) + "\n\n";

            text += "HUMAN \n";
            text += AnalysisTool.printWithoutTower(
                    new ArrayList<Tower>(Arrays.asList(new ArcherTower(), new IceTower(), new FireTower())), true,
                    mNodes);
            text += "\nALIEN \n";

            text += AnalysisTool.printWithoutTower(
                    new ArrayList<Tower>(Arrays
                            .asList(new ChainLightningTower(), new ParasiteTower(), new ShockTower())), false, mNodes);

            PrintWriter out = null;
            try {
                out = new PrintWriter("result.txt");
                out.println(text);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                out.close();
            }

            System.out.println(++done);
        } else {

            System.out.println("Skippiung " + file.getName());
        }

    }

}
