package nl.uva.td.ai;

import java.util.LinkedList;
import java.util.List;

import nl.uva.td.ai.mcts.MCTSAgent;
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
        GameField playerOneMap = Parser.parseFile(GameManager.MAP_FILE);
        Race race = new HumanRace();
        Agent agentOne = new MCTSAgent(race, playerOneMap.getTowerFields().size() * race.getAvailableTowerAmount() + 4,
                Player.PLAYER_ONE);

        race = new AlienRace();
        Agent agentTwo = new MCTSAgent(race, playerOneMap.getTowerFields().size() * race.getAvailableTowerAmount() + 4,
                Player.PLAYER_TWO);

        // Agent agentTwo = new SimpleAgent(Player.PLAYER_TWO, new AlienRace(),
        // "2;4;0;1;7;1;1;1;1;1;1;1;1;1;2;1;0;0;0;0;3;");

        long tic = System.currentTimeMillis();
        int trys = 0;
        GameResult result = null;
        boolean fixPlayerOne = false;
        boolean fixPlayerTwo = true;
        int runs = 0;
        for (int i = 0; i < 1000; ++i) {
            System.out.println("#########################################");
            do {
                agentOne.start(fixPlayerOne);
                agentTwo.start(fixPlayerTwo);

                result = GameManager.run(agentOne, agentTwo, fixPlayerOne, fixPlayerTwo);

                if (result.getWinner() != agentTwo.getPlayer()) {
                    // MCTS won against the last strategy!
                    PolicyQuality quality = calculatePolicyQuality(agentOne, agentTwo);

                    // TODO What if we have a draw but we beat many of the other policies? This
                    // policy is pretty good then.
                    result.setMultiplier(quality.getBeats().size() == 0 ? 1 : quality.getBeats().size());
                    agentOne.end(result, fixPlayerOne);
                    agentTwo.end(result, fixPlayerTwo);

                    // Evaluate if the found policy is good.
                    if (i != 0) {
                        PolicyDependencyGraph graph = getPolicyGraph(agentOne.getPlayer());
                        graph.addPolicy(getLastPolicy(agentOne), quality, getPolicyGraph(agentTwo.getPlayer()));
                    }

                    if (quality.isGoodQuality()) {
                        // This will stop the loop if the quality is good enough
                        // result.setWinner(agentOne.getPlayer());
                    }
                } else {
                    agentOne.end(result, fixPlayerOne);
                    agentTwo.end(result, fixPlayerTwo);
                }

                printInfo(agentOne, agentTwo, result, ++trys);
            } while (result.getWinner() != agentOne.getPlayer());

            // if (runs++ % 5 == 0) {
            // getPolicyGraph(agentOne.getPlayer()).prune(getPolicyGraph(agentTwo.getPlayer()));
            // }

            // getPolicyGraph(agentOne.getPlayer()).getPolicies().size() > 15

            System.err.println("##### -----> " + result.getSteps());

            System.out.println();
            printLastTactic(agentOne, agentTwo);

            agentTwo.reset();
            agentOne.resetFixedPolicy();

            System.out.println("Tries needed until better Policy " + trys);
            System.out.println();
            trys = 0;
            Agent tmp = agentOne;
            agentOne = agentTwo;
            agentTwo = tmp;
        }

        // int a = getPolicyGraph(agentOne.getPlayer()).test(getPolicyGraph(agentTwo.getPlayer()));
        // int b = getPolicyGraph(agentTwo.getPlayer()).test(getPolicyGraph(agentOne.getPlayer()));
        //
        // if (a != b) {
        // System.out.println("err");
        // }

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

    private static Policy getLastPolicy(final Agent agent) {
        return new Policy(getLastDecisionChain(agent), agent.getRace());
    }

    private static PolicyQuality calculatePolicyQuality(final Agent agentOne, final Agent agentTwo) {
        List<Integer> decisionChain = getLastDecisionChain(agentOne);

        Policy policyAgentOne = new Policy(decisionChain, agentOne.getRace());
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

                if (trys % (158000 * 40) == 0) {
                    GameManager.printStatistics();
                    agentOne.printStatistics();
                    System.out.println();
                }
            }
        }
    }
}
