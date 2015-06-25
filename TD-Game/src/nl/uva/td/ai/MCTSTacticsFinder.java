package nl.uva.td.ai;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import nl.uva.td.game.faction.alien.AlienRace;
import nl.uva.td.game.faction.human.HumanRace;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;

public class MCTSTacticsFinder {

    private static final String GOOD_ALIEN_TACTIC_1 = "25;22;13;4;28;2;3;12;3;9;3;3;1;21;0;3;1;3;2;1;3;1;0;0;0;0;2;1;1;0;0;3;2;0;1;2;0;1;0;3;3;0;1;0;0;0;3;2;1;2;0;0;2;2;0;0;1;1;0;1;1;0;1;1;1;2;0;0;2;1;2;2;0;3;0;2;0;0;0;0;2";

    private static final String GOOD_HUMAN_TACTIC_1 = "23;8;2;3;26;0;2;4;3;2;11;3;0;14;1;0;0;0;2;1;0;3;2;0;17;0;20;3;0;0;0;3;0;2;3;2;3;3;3;0;0;0;2;1;2;2;3;2;0;2;0;2;3;1;2;0;0;3;3;1;0;3;1;1;0;0;0;2;0;0;3;2;2;2;1;0;2;2;0;0;2;2;1;2;2;2;1;0;3;1;0;2;2;3;0;2;3;0;0;3;2";

    private static final String GOOD_HUMAN_TACTIC_2 = "23;17;26;0;0;14;3;2;5;1;1;0;11;2;0;20;8;3;2;3;0;0;1;0;2;2;1;2;2;3;0;1;3;0;3;2;1;3;1;0;0;3;3;0;2;0;3;0;3;3;2;2;3;1;0;0;0;2;2;2;0;0;0;0;0;2;0;3;2;1;2;0;3;2;2;3;0;0;3;1;1;3;0;0;3;2;0;0;0;2;3;2;3;1;0;0;2;0;2;1;0;0;2;2;0;2;0;0;0;2;2;0;1;3;2;2;1;0;2;";

    private static final String GOOD_HUMAN_TACTIC_3 = "18;2;0;0;0;3;0;11;3;1;2;0;6;3;21;8;0;13;1;22;0;1;27;0;3;0;0;1;0;0;0;2;2;0;3;0;2;1;1;1;0;3;0;0;2;2;1;0;0;2;2;1;2;3;0;0;0;0;0;0;1;3;3;1;0;2;3;2;0;1;0;0;0;0;3;3;0;0;0;0;0;1;1;3;1;0;0;0;0;0;2;0;1;3;0;3;3;0;1;0;1;1;3;0;3;3;1;3;0;0;0;1;0;2;1;2;";

    private static final String GOOD_HUMAN_TACTIC_4 = "18;2;2;2;2;3;2;11;3;1;2;2;6;3;21;8;2;13;1;22;2;1;27;2;3;2;2;1;2;2;2;2;2;2;3;2;2;1;1;1;2;3;2;2;2;2;1;2;2;2;2;1;2;3;0;0;0;0;0;0;1;3;3;1;0;2;3;2;0;1;0;0;0;0;3;3;0;0;0;0;0;1;1;3;1;0;0;0;0;0;2;0;1;3;0;3;3;0;1;0;1;1;3;0;3;3;1;3;0;0;0;1;0;2;1;2;";

    private static final PolicyDependencyGraph sPlayerOne = new PolicyDependencyGraph();

    private static final PolicyDependencyGraph sPlayerTwo = new PolicyDependencyGraph();

    public static void main(final String[] args) {
        // createFighters();
        fight();
    }

    public static void fight() {
        long tic = System.currentTimeMillis();
        List<Policy> humanPolicyList = readFromFile(new HumanRace());
        List<Policy> alienPolicyList = readFromFile(new AlienRace());

        ActionNode currentNode = new ActionNode(0, Race.Type.ALIEN);
        final ActionNode startingNode = currentNode;

        for (Policy humanPolicy : humanPolicyList) {

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
                    currentNode = addOrReturnNode(currentNode, humanPolicy.getNextAction(), humanPolicy.getRace()
                            .getType());

                    if (relation == Relation.BETTER) {
                        currentNode.winCounter++;
                    } else if (relation == Relation.EQUALS) {
                        currentNode.drawCounter++;
                    } else {
                        currentNode.loseCounter++;
                    }

                    currentNode = addOrReturnNode(currentNode, alienPolicy.getNextAction(), alienPolicy.getRace()
                            .getType());

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
        try {
            writer = new PrintWriter("neo4jdb-action-nodes" + ".txt", "UTF-8");
        } catch (Exception e1) {}
        startingNode.buildNodeJOutput(writer);

        writer.close();
        System.out.println((System.currentTimeMillis() - tic) / 1000 + "s");
    }

    public static ActionNode addOrReturnNode(final ActionNode currentNode, final int action, final Race.Type raceType) {
        if (currentNode.children.containsKey(action)) {
            return currentNode.children.get(action);
        }

        ActionNode node = new ActionNode(action, raceType);
        currentNode.children.put(action, node);
        return node;
    }

    public static void createFighters() {
        GameField playerOneMap = Parser.parseFile(GameManager.MAP_FILE);
        Race race = new HumanRace();

        Agent agentOne = new MCTSAgent(race, playerOneMap.getTowerFields().size() * race.getAvailableTowerAmount() + 4,
                Player.PLAYER_ONE);

        race = new AlienRace();
        Agent agentTwo = new MCTSAgent(race, playerOneMap.getTowerFields().size() * race.getAvailableTowerAmount() + 4,
                Player.PLAYER_TWO);

        long tic = System.currentTimeMillis();
        int trys = 0;
        int amountOfReocuringPolicies = 0;

        GameResult result = null;
        boolean fixPlayerOne = false;
        boolean fixPlayerTwo = true;

        Set<Policy> humanPolicies = new HashSet<Policy>();
        Set<Policy> alienPolicies = new HashSet<Policy>();

        final int amountOfCounterPolicySearches = 1000;

        for (int i = 0; i < amountOfCounterPolicySearches; ++i) {

            do {
                agentOne.start(fixPlayerOne);
                agentTwo.start(fixPlayerTwo);

                result = GameManager.run(agentOne, agentTwo, fixPlayerOne, fixPlayerTwo);

                if (result.getWinner() == agentOne.getPlayer()) {

                    agentOne.end(result, fixPlayerOne);
                    agentTwo.end(result, fixPlayerTwo);

                    if (agentOne.getRace().getName() == AlienRace.NAME) {
                        alienPolicies.add(agentOne.getLastUsedPolicy());
                    } else {
                        humanPolicies.add(agentOne.getLastUsedPolicy());
                    }

                } else {
                    agentOne.end(result, fixPlayerOne);
                    agentTwo.end(result, fixPlayerTwo);
                }
            } while (result.getWinner() != agentOne.getPlayer());

            agentTwo.reset();
            agentOne.resetFixedPolicy();

            trys = 0;
            Agent tmp = agentOne;
            agentOne = agentTwo;
            agentTwo = tmp;
        }

        saveToFile(alienPolicies, new AlienRace());
        saveToFile(humanPolicies, new HumanRace());
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
