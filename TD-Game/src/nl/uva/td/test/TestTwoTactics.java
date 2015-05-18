package nl.uva.td.test;

import nl.uva.td.ai.Agent;
import nl.uva.td.ai.SimpleAgent;
import nl.uva.td.game.GameManager;
import nl.uva.td.game.GameManager.Player;
import nl.uva.td.game.GameResult;
import nl.uva.td.game.faction.alien.AlienRace;
import nl.uva.td.game.faction.human.HumanRace;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;

public class TestTwoTactics {

    private static String sTacticAlien = "7;4;3;0;0;1;1;1;3;1;3;1;2;1;1;1;3;1;0;1;1;0;0;0";

    private static String sTacticHuman = "5;8;3;1;1;0;3;1;2;1;1;3;3;3;1;3;1;3;1;3;0;0;0;0";

    public static void main(final String[] args) {
        int draw = 0;
        int playerOne = 0;
        int playerTwo = 0;

        int maxLength = 5;
        int counter = 0;
        for (int length = 1; length <= maxLength; ++length) {
            for (int a = 0; a <= length; a++) {
                for (int b = length - a; b >= 0; b--) {
                    int c = (length - a) - b;

                    for (int i = 0; i < a; i++) {
                        System.out.print(1 + ",");
                    }
                    for (int i = 0; i < b; i++) {
                        System.out.print(2 + ",");
                    }
                    for (int i = 0; i < c; i++) {
                        System.out.print(3 + ",");
                    }
                    System.out.println(++counter);
                }
            }
        }

        for (int i = 0; i < 20; i++) {

            GameField playerOneMap = Parser.parseFile(GameManager.MAP_FILE);
            Agent agentOne = new SimpleAgent(Player.PLAYER_ONE, new HumanRace(), sTacticHuman);
            Agent agentTwo = new SimpleAgent(Player.PLAYER_TWO, new AlienRace(), sTacticAlien);

            GameResult result = null;
            boolean fixPlayerOne = false;
            boolean fixPlayerTwo = true;

            agentOne.start();
            agentTwo.start();

            result = GameManager.run(agentOne, agentTwo, fixPlayerOne, fixPlayerTwo);

            if (result.getWinner() == Player.PLAYER_ONE) {
                playerOne++;
            } else if (result.getWinner() == Player.PLAYER_TWO) {
                playerTwo++;
            } else {
                draw++;
            }
        }

        System.out.println("Human: " + playerOne + " Alien: " + playerTwo + " Draw: " + draw);
    }

}
