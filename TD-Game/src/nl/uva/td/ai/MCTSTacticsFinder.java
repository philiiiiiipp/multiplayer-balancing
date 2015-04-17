package nl.uva.td.ai;

import nl.uva.td.game.GameManager;
import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.faction.Race;
import nl.uva.td.game.faction.alien.AlienRace;
import nl.uva.td.game.faction.human.HumanRace;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;

public class MCTSTacticsFinder {

    private static final String GOOD_HUMAN_TACTIC_1 = "23;8;2;3;26;0;2;4;3;2;11;3;0;14;1;0;0;0;2;1;0;3;2;0;17;0;20;3;0;0;0;3;0;2;3;2;3;3;3;0;0;0;2;1;2;2;3;2;0;2;0;2;3;1;2;0;0;3;3;1;0;3;1;1;0;0;0;2;0;0;3;2;2;2;1;0;2;2;0;0;2;2;1;2;2;2;1;0;3;1;0;2;2;3;0;2;3;0;0;3;2";

    private static final String GOOD_HUMAN_TACTIC_2 = "23;17;26;0;0;14;3;2;5;1;1;0;11;2;0;20;8;3;2;3;0;0;1;0;2;2;1;2;2;3;0;1;3;0;3;2;1;3;1;0;0;3;3;0;2;0;3;0;3;3;2;2;3;1;0;0;0;2;2;2;0;0;0;0;0;2;0;3;2;1;2;0;3;2;2;3;0;0;3;1;1;3;0;0;3;2;0;0;0;2;3;2;3;1;0;0;2;0;2;1;0;0;2;2;0;2;0;0;0;2;2;0;1;3;2;2;1;0;2;";

    private static final String GOOD_HUMAN_TACTIC_3 = "18;2;0;0;0;3;0;11;3;1;2;0;6;3;21;8;0;13;1;22;0;1;27;0;3;0;0;1;0;0;0;2;2;0;3;0;2;1;1;1;0;3;0;0;2;2;1;0;0;2;2;1;2;3;0;0;0;0;0;0;1;3;3;1;0;2;3;2;0;1;0;0;0;0;3;3;0;0;0;0;0;1;1;3;1;0;0;0;0;0;2;0;1;3;0;3;3;0;1;0;1;1;3;0;3;3;1;3;0;0;0;1;0;2;1;2;";

    private static final String GOOD_HUMAN_TACTIC_4 = "18;2;2;2;2;3;2;11;3;1;2;2;6;3;21;8;2;13;1;22;2;1;27;2;3;2;2;1;2;2;2;2;2;2;3;2;2;1;1;1;2;3;2;2;2;2;1;2;2;2;2;1;2;3;0;0;0;0;0;0;1;3;3;1;0;2;3;2;0;1;0;0;0;0;3;3;0;0;0;0;0;1;1;3;1;0;0;0;0;0;2;0;1;3;0;3;3;0;1;0;1;1;3;0;3;3;1;3;0;0;0;1;0;2;1;2;";

    public static void main(final String[] args) {
        GameField playerOneMap = Parser.parseFile(GameManager.MAP_FILE);
        Race race = new HumanRace();
        // Agent agentOne = new MCTSAgent(race, playerOneMap.getTowerFields().size() *
        // race.getAvailableTowerAmount() + 4,
        // Player.PLAYER_ONE);

        Agent agentOne = new SimpleAgent(Player.PLAYER_ONE, new HumanRace(), GOOD_HUMAN_TACTIC_4);

        race = new AlienRace();
        Agent agentTwo = new SimpleAlienAgent(Player.PLAYER_TWO);
        // new MCTSAgent(race, playerOneMap.getTowerFields().size() * race.getAvailableTowerAmount()
        // + 4,
        // Player.PLAYER_TWO);

        long tic = System.currentTimeMillis();
        int trys = 0;
        Player winner = null;
        boolean fixPlayerOne = false;
        boolean fixPlayerTwo = true;
        for (int i = 0; i < 200000; ++i) {

            do {
                agentOne.start();
                agentTwo.start();

                winner = GameManager.run(agentOne, agentTwo, fixPlayerOne, fixPlayerTwo);

                agentOne.end(winner, fixPlayerOne);
                agentTwo.end(winner, fixPlayerTwo);

                if (agentOne.getPlayer() == Player.PLAYER_ONE) {
                    if (++trys % 1000 == 0) {
                        System.out.print("#");
                    }
                    if (trys % 158000 == 0) {
                        System.out.println(" " + trys);
                        GameManager.printStatistics();
                        agentOne.printStatistics();
                        System.out.println();
                    }
                } else {
                    if (trys++ % 1000 == 0) {
                        System.err.print("#");
                    }
                    if (trys % 158000 == 0) {
                        System.err.println(" " + trys);
                        GameManager.printStatistics();
                        agentOne.printStatistics();
                        System.err.println();
                    }
                }
            } while (winner != agentOne.getPlayer());

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

        tic = System.currentTimeMillis() - tic;
        GameManager.printStatistics();
        System.out.println("Total: " + tic / 1000 + "s");
        GameManager.resetStatistics();
    }

    private static void printLastTactic(final Agent playerOne, final Agent playerTwo) {
        String playerOneTactic = "";
        String playerTwoTactic = "";

        if (playerOne.getLastDecisionChain().size() != playerTwo.getLastDecisionChain().size()) {
            throw new RuntimeException("Must be the same size");
        }

        for (int i = 0; i < playerOne.getLastDecisionChain().size(); ++i) {
            playerOneTactic += playerOne.getLastDecisionChain().get(i);
            playerTwoTactic += playerTwo.getLastDecisionChain().get(i);
        }
        for (int i = 0; i < playerOne.getLastDecisionChain().size(); ++i) {
            playerOneTactic += playerOne.getLastDecisionChain().get(i).getDecisionNumber() + ";";
            playerTwoTactic += playerTwo.getLastDecisionChain().get(i).getDecisionNumber() + ";";
        }

        System.out.println("Search: " + playerOne.getRace().getName() + ": " + playerOneTactic);
        System.out.println("Fixed:  " + playerTwo.getRace().getName() + ": " + playerTwoTactic);
        System.out.println("Steps: " + playerOne.getLastDecisionChain().size());
        playerOne.printStatistics();
        System.out.println("---------------------------------");
    }
}
