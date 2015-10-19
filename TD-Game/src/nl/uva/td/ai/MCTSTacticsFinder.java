package nl.uva.td.ai;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import nl.uva.td.ai.PolicyQuality.Relation;
import nl.uva.td.ai.mcts.BruteForceAgent;
import nl.uva.td.ai.mcts.BruteForceStepAgent;
import nl.uva.td.ai.mcts.MCTSAgent;
import nl.uva.td.ai.quality.ActionNode;
import nl.uva.td.game.GameManager;
import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.GameResult;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.faction.Race.Type;
import nl.uva.td.game.faction.alien.AlienRace;
import nl.uva.td.game.faction.alien.tower.ChainLightningTower;
import nl.uva.td.game.faction.alien.tower.ParasiteTower;
import nl.uva.td.game.faction.alien.tower.ShockTower;
import nl.uva.td.game.faction.human.HumanRace;
import nl.uva.td.game.faction.human.tower.ArcherTower;
import nl.uva.td.game.faction.human.tower.FireTower;
import nl.uva.td.game.faction.human.tower.IceTower;
import nl.uva.td.game.faction.tower.Frozen;
import nl.uva.td.game.faction.tower.Parasite;
import nl.uva.td.game.faction.tower.Shock;
import nl.uva.td.game.faction.tower.Tower;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.visual.AnalysisTool;

public class MCTSTacticsFinder {

    private static PolicyDependencyGraph sPlayerOne = new PolicyDependencyGraph();

    private static PolicyDependencyGraph sPlayerTwo = new PolicyDependencyGraph();

    public static void main(final String[] args) {

        String name = "action-nodes-graph-3x3-SettingD_";
        List<double[]> towerImpact = new ArrayList<double[]>();
        for (int i = 1; i < 4; i++) {

            AnalysisTool.FILENAME = name + i;

            createFighters();

            System.out.println("Created contestants");

            fight();

            sPlayerOne = new PolicyDependencyGraph();
            sPlayerTwo = new PolicyDependencyGraph();

            AnalysisTool.mNodes = MCTSTacticsFinder.readNodes();

            System.out.println("Done fighting");

            // useContestant();

            System.out.println("Done");

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
            text += "Parasite Dmg: " + tower.getDamage() + " Range: " + tower.getRange() + " Special: "
                    + "Backwards for " + Parasite.PARASITE_DURATION + " steps\n";

            tower = new ShockTower();
            text += "Shock Dmg: " + tower.getDamage() + " Range: " + tower.getRange() + " Special: " + "Stop for "
                    + Shock.SHOCK_DURATION + " steps\n";

            text += AnalysisTool.getChildTable(AnalysisTool.mNodes) + "\n";
            text += ActionNode.getActionInfo() + "\n";

            text += ActionNode.createLatexTable(ActionNode.humanInfo) + "\n";
            text += ActionNode.createLatexTable(ActionNode.alienInfo) + "\n";

            text += "HUMAN \n";
            text += AnalysisTool.printWithoutTower(
                    new ArrayList<Tower>(Arrays.asList(new ArcherTower(), new IceTower(), new FireTower())), true,
                    AnalysisTool.mNodes);
            text += "\nALIEN \n";

            text += AnalysisTool.printWithoutTower(
                    new ArrayList<Tower>(Arrays
                            .asList(new ChainLightningTower(), new ParasiteTower(), new ShockTower())), false,
                    AnalysisTool.mNodes);

            System.out.println(text);

            PrintWriter out = null;
            try {
                out = new PrintWriter(AnalysisTool.FILENAME + "-result");
                out.println(text);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                out.close();
            }

            towerImpact.add(AnalysisTool.sTowerImpact);
            AnalysisTool.sTowerImpact = new double[6];

            AnalysisTool.mNodes = null;
            ActionNode.reset();
        }

        System.out.println();

        // Locale locale = new Locale("da", "DK");
        // String pattern = "###.######";

        // DecimalFormat format = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        // format.applyPattern(pattern);
        PrintWriter out = null;
        String impact = "";
        try {
            out = new PrintWriter(AnalysisTool.FILENAME + "-result-complete");
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
            otherSymbols.setDecimalSeparator(',');
            final DecimalFormat format = new DecimalFormat("###.#####", otherSymbols);
            impact += "Chain Lightning\t";
            for (int i = 0; i < towerImpact.size(); ++i) {
                impact += format.format(towerImpact.get(i)[3]) + "\t";
            }
            impact += "\n";

            impact += "Parasite\t";
            for (int i = 0; i < towerImpact.size(); ++i) {
                impact += format.format(towerImpact.get(i)[4]) + "\t";
            }
            impact += "\n";

            impact += "Schock\t";
            for (int i = 0; i < towerImpact.size(); ++i) {
                impact += format.format(towerImpact.get(i)[5]) + "\t";
            }
            impact += "\n";

            impact += "Archer\t";
            for (int i = 0; i < towerImpact.size(); ++i) {
                impact += format.format(towerImpact.get(i)[0]) + "\t";
            }
            impact += "\n";

            impact += "Ice\t";
            for (int i = 0; i < towerImpact.size(); ++i) {
                impact += format.format(towerImpact.get(i)[1]) + "\t";
            }
            impact += "\n";

            impact += "Fire\t";
            for (int i = 0; i < towerImpact.size(); ++i) {
                impact += format.format(towerImpact.get(i)[2]) + "\t";
            }
            impact += "\n";

            out.println(impact);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            out.close();
        }

        System.out.println(impact);
    }

    public static final String CONSIDER_ALL = "X";

    public static final String SEPERATOR = " ";

    public static final String DECISION_CHAIN = "";

    public static void useContestant() {
        List<ActionNode> nextNodes = readNodes();
        // System.out.println(ActionNode.lastUsedID);
        System.out.println("Done Reading");

        String arg = "0;";
        Short nextAction = Short.parseShort(arg.substring(0, arg.indexOf(SEPERATOR)));
        String leftoverArgument = arg.substring(arg.indexOf(SEPERATOR) + 1);

        for (ActionNode node : nextNodes) {
            if (node.action == nextAction) {
                try {
                    getValues(node, leftoverArgument);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            }
        }

        // ActionNode[] alienNodes = new ActionNode[19];
        // for (short i = 0; i < alienNodes.length; ++i) {
        // alienNodes[i] = new ActionNode(i, Type.ALIEN, null);
        // }
        //
        // System.out.println("---- HUMAN ----");
        // for (ActionNode node : nextNodes) {
        // for (ActionNode child : node.children.values()) {
        // alienNodes[child.action].winCounter += child.winCounter;
        // alienNodes[child.action].drawCounter += child.drawCounter;
        // alienNodes[child.action].loseCounter += child.loseCounter;
        //
        // if (child.action == 16) {
        // System.out.println(node.action + " -> " + child);
        // }
        // }
        // }
    }

    public static void getValues(final ActionNode current, final String arg) throws Exception {
        if (arg.length() == 0) {
            printActionNodePath(current);
            System.out.println(current);
            System.out.println();

            for (ActionNode node : current.mChildren) {

                System.out.println(node);
            }

            printActionNodePath(current);
            return;
        }

        Short nextAction = Short.parseShort(arg.substring(0, arg.indexOf(SEPERATOR)));
        String leftoverArgument = arg.substring(arg.indexOf(SEPERATOR) + 1);
        ActionNode nextActionNode = current.getChild(nextAction);

        getValues(nextActionNode, leftoverArgument);
    }

    public static ActionNode getNode(final ActionNode current, final String arg) throws Exception {
        if (arg.length() == 0) {
            return current;
        }

        Short nextAction = Short.parseShort(arg.substring(0, arg.indexOf(SEPERATOR)));
        String leftoverArgument = arg.substring(arg.indexOf(SEPERATOR) + 1);
        ActionNode nextActionNode = current.getChild(nextAction);

        return getNode(nextActionNode, leftoverArgument);
    }

    public static void find(final ActionNode node) {
        if (node.winCounter + node.drawCounter + node.loseCounter == 1) {
            return;
        }

        if (node.loseCounter == 0) {
            ActionNode current = node;
            while (current != null) {
                System.out.print(new Decision(current.action, Race.getRaceForType((current.isHuman ? Type.HUMAN
                        : Type.ALIEN))) + "<-");
                current = current.parent;
            }

            System.out.println(" \t \t W:" + node.winCounter + "\t D:" + node.drawCounter + "\t L:" + node.loseCounter
                    + "\t " + (node.isHuman ? Type.HUMAN : Type.ALIEN));
        } else {
            for (ActionNode child : node.mChildren) {

                find(child);
            }
        }
    }

    public static void printActionNodePath(ActionNode node) {
        while (node != null) {
            System.out.print(new Decision(node.action, Race.getRaceForType((node.isHuman ? Type.HUMAN : Type.ALIEN)))
                    + "<-");
            node = node.parent;
        }
        System.out.println();
    }

    public static List<ActionNode> fight() {
        long tic = System.currentTimeMillis();
        List<Policy> humanPolicyList = readFromFile(new HumanRace());
        List<Policy> alienPolicyList = readFromFile(new AlienRace());

        ActionNode currentNode = new ActionNode((short) 0, Race.Type.ALIEN, null);
        final ActionNode startingNode = currentNode;

        int counter = 0;
        for (Policy humanPolicy : humanPolicyList) {
            System.out.println(counter++ + "/" + humanPolicyList.size());
            for (Policy alienPolicy : alienPolicyList) {

                GameResult result = GameManager.run(humanPolicy, alienPolicy);
                PolicyQuality.Relation relation = Relation.EQUALS;

                if (result.getWinner() == Player.PLAYER_ONE) {
                    relation = Relation.BETTER;
                } else if (result.getWinner() == Player.PLAYER_TWO) {
                    relation = Relation.WORSE;
                }

                humanPolicy.reset();
                alienPolicy.reset();

                for (int step = 0; step < result.getSteps(); ++step) {
                    currentNode = addOrReturnNode(currentNode, (short) humanPolicy.getNextAction(), humanPolicy
                            .getRace().getType());

                    if (relation == Relation.BETTER) {
                        currentNode.winCounter++;
                    } else if (relation == Relation.EQUALS) {
                        currentNode.drawCounter++;
                    } else {
                        currentNode.loseCounter++;
                    }

                    currentNode = addOrReturnNode(currentNode, (short) alienPolicy.getNextAction(), alienPolicy
                            .getRace().getType());

                    if (relation == Relation.BETTER) {
                        currentNode.loseCounter++;
                    } else if (relation == Relation.EQUALS) {
                        currentNode.drawCounter++;
                    } else {
                        currentNode.winCounter++;
                    }
                }

                currentNode = startingNode;
                humanPolicy.reset();
                alienPolicy.reset();
            }
        }

        PrintWriter writer = null;
        // try {
        // writer = new PrintWriter("neo4jdb-action-nodes.txt", "UTF-8");
        // } catch (Exception e1) {}
        // startingNode.buildNodeJOutput(writer);
        //
        // writer.close();

        try {
            writer = new PrintWriter(AnalysisTool.FILENAME, "UTF-8");

            for (ActionNode child : startingNode.mChildren) {
                child.saveGraph(writer);
            }

            writer.println(ActionNode.END_OF_CHILDREN);
        } catch (Exception e1) {

        } finally {
            writer.close();
        }

        Tower tower = new FireTower();
        String text = "Fire Dmg: " + tower.getDamage() + " Range: " + tower.getRange() + " Special: Does splash\n";

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

        try {
            writer = new PrintWriter(AnalysisTool.FILENAME + "-info", "UTF-8");
            writer.print(text);
        } catch (Exception e1) {} finally {
            writer.close();
        }

        System.out.println((System.currentTimeMillis() - tic) / 1000 + "s");

        return startingNode.mChildren;
    }

    public static ActionNode addOrReturnNode(final ActionNode currentNode, final short action, final Race.Type raceType) {
        ActionNode result = currentNode.getChild(action);
        if (result != null) {
            return result;
        }

        ActionNode node = new ActionNode(action, raceType, currentNode);
        currentNode.mChildren.add(node);
        return node;
    }

    public static void createFighters() {
        GameField playerOneMap = Parser.parseFile(GameManager.MAP_FILE);
        Race race = new HumanRace();

        MCTSAgent agentOne = new MCTSAgent(race, playerOneMap.getTowerFields().size() * race.getAvailableTowerAmount()
                + 4, Player.PLAYER_ONE);

        race = new AlienRace();
        MCTSAgent agentTwo = new MCTSAgent(race, playerOneMap.getTowerFields().size() * race.getAvailableTowerAmount()
                + 4, Player.PLAYER_TWO);

        long tic = System.currentTimeMillis();
        int trys = 0;
        int amountOfReocuringPolicies = 0;

        GameResult result = null;
        boolean fixPlayerOne = false;
        boolean fixPlayerTwo = true;

        Set<Policy> humanPolicies = new HashSet<Policy>();
        Set<Policy> alienPolicies = new HashSet<Policy>();

        final int amountOfCounterPolicySearches = 100000;
        final int amountOfPoliciesToConsider = 1800;
        final int maxSeachingCounter = 1000;

        for (int i = 0; i < amountOfCounterPolicySearches; ++i) {
            if (i % (amountOfCounterPolicySearches / 10) == 0) {
                System.out.println(i);
            }

            int counter = 0;
            do {
                agentOne.start(fixPlayerOne);
                agentTwo.start(fixPlayerTwo);

                result = GameManager.run(agentOne, agentTwo, fixPlayerOne, fixPlayerTwo);

                if (result.getWinner() == agentOne.getPlayer()) {

                    agentOne.end(result, fixPlayerOne);
                    agentTwo.end(result, fixPlayerTwo);

                    if (i > amountOfCounterPolicySearches - amountOfPoliciesToConsider) {
                        if (agentOne.getRace().getName() == AlienRace.NAME) {
                            alienPolicies.add(agentOne.getLastUsedPolicy());
                        } else {
                            humanPolicies.add(agentOne.getLastUsedPolicy());
                        }
                    }
                } else {
                    agentOne.end(result, fixPlayerOne);
                    agentTwo.end(result, fixPlayerTwo);
                }

                if (++counter % 10000 == 0) {
                    System.out.println(result.getWinner());
                    System.out.println(agentOne.getLastUsedPolicy());
                    System.out.println(agentTwo.getLastUsedPolicy());
                    System.out.println("Taking long " + agentOne.getRace().getType());

                    if (counter % (10 * 10000) == 0) {
                        System.out.println("------------- RESETING --------------");
                        agentOne.initialiseSearchTree();
                    }
                }
            } while (result.getWinner() != agentOne.getPlayer()); // ++counter != maxSeachingCounter
            // &&

            if (counter == maxSeachingCounter) {
                System.out.println("Counter reached");
                System.out.println(agentOne.getRace().getType());
            }

            agentTwo.reset();
            agentOne.resetFixedPolicy();

            trys = 0;
            MCTSAgent tmp = agentOne;
            agentOne = agentTwo;
            agentTwo = tmp;
        }

        saveToFile(alienPolicies, new AlienRace());
        saveToFile(humanPolicies, new HumanRace());
    }

    public static List<ActionNode> readNodes() {
        return readNodes(AnalysisTool.FILENAME);
    }

    public static List<ActionNode> readNodes(final String file) {
        List<ActionNode> result = new LinkedList<ActionNode>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String currentLine;
        try {
            while ((currentLine = reader.readLine()) != null && !currentLine.equals(ActionNode.END_OF_CHILDREN)) {
                String[] arguments = currentLine.split(";");

                short action = Short.parseShort(arguments[0]);
                Race.Type type = Integer.parseInt(arguments[1]) == Race.Type.HUMAN.ordinal() ? Race.Type.HUMAN
                        : Race.Type.ALIEN;

                int winCounter = Integer.parseInt(arguments[2]);
                int drawCounter = Integer.parseInt(arguments[3]);
                int loseCounter = Integer.parseInt(arguments[4]);
                ActionNode nextNode = new ActionNode(action, type, winCounter, drawCounter, loseCounter, null, 1);
                result.add(nextNode);

                ActionNode.readGraph(reader, nextNode, 2);
            }
        } catch (NumberFormatException | IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // String currentLine;
        // ActionNode toTest = null;
        // try {
        // currentLine = reader.readLine();
        // String[] arguments = currentLine.split(";");
        //
        // int action = Integer.parseInt(arguments[0]);
        // Race.Type type = Integer.parseInt(arguments[1]) == Race.Type.HUMAN.ordinal() ?
        // Race.Type.HUMAN
        // : Race.Type.ALIEN;
        //
        // int winCounter = Integer.parseInt(arguments[2]);
        // int drawCounter = Integer.parseInt(arguments[3]);
        // int loseCounter = Integer.parseInt(arguments[4]);
        // toTest = new ActionNode(action, type, winCounter, drawCounter, loseCounter, null);
        // toTest.readGraph(reader);
        //
        // } catch (NumberFormatException | IOException e1) {
        // // TODO Auto-generated catch block
        // e1.printStackTrace();
        // }

        // close the BufferedReader when we're done
        try {
            reader.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    public static List<Policy> readFromFile(final Race race) {
        String raceTactics = null;

        try {
            raceTactics = new String(Files.readAllBytes(Paths.get(race.getName() + ".txt")));
        } catch (final IOException ex) {
            throw new RuntimeException("File " + race.getName() + ".txt not found.");
        }

        List<Policy> policyList = new LinkedList<Policy>();
        String[] policyStrings = raceTactics.split("\n");
        for (String policy : policyStrings) {

            String[] actions = policy.split(";");
            List<Integer> actionList = new LinkedList<Integer>();

            for (String action : actions) {
                actionList.add(Integer.parseInt(action));
            }

            policyList.add(new Policy(actionList, race));
        }

        return policyList;
    }

    public static void saveToFile(final Set<Policy> policies, final Race race) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(race.getName() + ".txt", "UTF-8");
        } catch (Exception e1) {}

        for (Policy p : policies) {
            for (Integer action : p.getActionList()) {
                writer.print(action + ";");
            }
            writer.println();
        }

        writer.close();
    }

    private static Policy getLastPolicy(final Agent agent) {
        return new Policy(getLastDecisionChain(agent), agent.getRace());
    }

    private static PolicyQuality calculatePolicyQuality(final Policy policy, final PolicyDependencyGraph enemyGraph) {

        PolicyQuality policyQuality = new PolicyQuality();
        for (Policy p : enemyGraph.getPolicies()) {
            GameResult result = GameManager.run(policy, p);

            if (result.getWinner() == Player.PLAYER_ONE) {
                policyQuality.beats(new GameInfo(p, result.getSteps()));
            } else if (result.getWinner() == Player.NONE) {
                policyQuality.draws(new GameInfo(p, result.getSteps()));
            } else {
                policyQuality.loses(new GameInfo(p, result.getSteps()));
            }

            p.reset();
            policy.reset();
        }

        return policyQuality;
    }

    private static PolicyQuality calculatePolicyQuality(final Agent agentOne, final Agent agentTwo) {
        List<Integer> decisionChain = getLastDecisionChain(agentOne);

        Policy policyAgentOne = new Policy(decisionChain, agentOne.getRace());
        if (getPolicyGraph(agentOne.getPlayer()).getPolicies().contains(policyAgentOne)) {
            return getPolicyGraph(agentOne.getPlayer()).getPolicyQuality(policyAgentOne);
        }

        PolicyDependencyGraph agentTwoPolicies = getPolicyGraph(agentTwo.getPlayer());

        PolicyQuality policyQuality = new PolicyQuality();
        for (Policy p : agentTwoPolicies.getPolicies()) {
            GameResult result = GameManager.run(policyAgentOne, p);

            if (result.getWinner() == Player.PLAYER_ONE) {
                policyQuality.beats(new GameInfo(p, result.getSteps()));
            } else if (result.getWinner() == Player.NONE) {
                policyQuality.draws(new GameInfo(p, result.getSteps()));
            } else {
                policyQuality.loses(new GameInfo(p, result.getSteps()));
            }

            p.reset();
            policyAgentOne.reset();
        }

        return policyQuality;
    }

    private static List<Integer> getLastDecisionChain(final Agent agentOne) {
        List<Integer> decisionChain = new LinkedList<Integer>();
        for (Decision d : agentOne.getLastDecisionChain()) {
            decisionChain.add(d.getDecisionNumber());
        }

        return decisionChain;
    }

    private static PolicyDependencyGraph getPolicyGraph(final Player player) {
        if (player == Player.PLAYER_ONE) {
            return sPlayerOne;
        } else {
            return sPlayerTwo;
        }
    }

    private static void printLastTactic(final Agent playerOne, final Agent playerTwo) {
        String playerOneTactic = "";
        String playerOneTacticInts = "";
        String playerTwoTactic = "";
        String playerTwoTacticInts = "";

        if (playerOne.getLastDecisionChain().size() != playerTwo.getLastDecisionChain().size()) {
            throw new RuntimeException("Must be the same size");
        }

        for (int i = 0; i < playerOne.getLastDecisionChain().size(); ++i) {
            playerOneTactic += playerOne.getLastDecisionChain().get(i);
            playerOneTacticInts += playerOne.getLastDecisionChain().get(i).getDecisionNumber() + ";";
            playerTwoTactic += playerTwo.getLastDecisionChain().get(i);
            playerTwoTacticInts += playerTwo.getLastDecisionChain().get(i).getDecisionNumber() + ";";
        }

        System.out.println("Search: " + playerOne.getRace().getName() + ": " + playerOneTactic);
        System.out.println(playerOneTacticInts);
        System.out.println("Fixed:  " + playerTwo.getRace().getName() + ": " + playerTwoTactic);
        System.out.println(playerTwoTacticInts);
        System.out.println("Steps: " + playerOne.getLastDecisionChain().size());
        playerOne.printStatistics();
        System.out.println("---------------------------------");
    }

    private static void printInfo(final Agent agentOne, final Agent agentTwo, final GameResult result, final int trys) {
        // debug info
        if (trys % 1000 == 0) {
            System.out.print("#");

            if (trys % 158000 == 0) {
                System.out.println(" " + trys);

                if (trys % (158000 * 5) == 0) {
                    GameManager.printStatistics();
                    agentOne.printStatistics();
                    System.out.println();
                }
            }
        }
    }

    public static void bruteForceFight() {
        GameField playerOneMap = Parser.parseFile(GameManager.MAP_FILE);

        Race race = new AlienRace();
        BruteForceStepAgent agentOne = new BruteForceStepAgent(Player.PLAYER_ONE, race);

        race = new HumanRace();
        BruteForceAgent agentTwo = new BruteForceAgent(Player.PLAYER_TWO, race);

        long tic = System.currentTimeMillis();
        int trys = 0;
        GameResult result = null;
        boolean fixPlayerOne = false;
        boolean fixPlayerTwo = false;

        for (; !agentOne.mDone;) {

            do {
                agentOne.start(fixPlayerOne);
                agentTwo.start(fixPlayerTwo);

                result = GameManager.run(agentOne, agentTwo, fixPlayerOne, fixPlayerTwo);
                agentTwo.end(result, fixPlayerTwo);

                if (++trys % 157000 == 0) {
                    printLastTactic(agentOne, agentTwo);
                }

            } while (result.getWinner() == agentOne.getPlayer() && !agentTwo.mDone);

            if (result.getWinner() == agentOne.getPlayer()) {
                // This tactic won against all!
                System.out.println("Winner Winner Chicken Dinner! "
                        + new Policy(agentOne.getRace(), agentOne.getLastDecisionChain()));

            }

            // System.out.println();
            // printLastTactic(agentOne, agentTwo);

            agentOne.end(result, false);
            agentTwo.reset();
            // trys = 0;
        }

        boolean donePruning = false;
        while (!donePruning) {
            donePruning = !getPolicyGraph(agentOne.getPlayer()).prune(getPolicyGraph(agentTwo.getPlayer()));
            donePruning = !getPolicyGraph(agentTwo.getPlayer()).prune(getPolicyGraph(agentOne.getPlayer()))
                    || donePruning;

            System.out.println("pruned");
        }

        getPolicyGraph(agentOne.getPlayer()).info();
        tic = System.currentTimeMillis() - tic;
        GameManager.printStatistics();
        System.out.println("Total: " + tic / 1000 + "s");
        GameManager.resetStatistics();
    }
}
