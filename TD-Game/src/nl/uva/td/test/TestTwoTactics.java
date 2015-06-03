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

    private static String sTacticAlien = "2;4;1;1;1;1;2;1;1;1;0;1;1;1;1;1;0;0;0;1;1;2;";

    private static String sTacticHuman = "8;3;3;3;3;3;1;1;1;3;3;5;1;1;1;3;3;";

    public static void main(final String[] args) {

        int draw = 0;
        int playerOne = 0;
        int playerTwo = 0;

        for (int i = 0; i < 20; i++) {

            GameField playerOneMap = Parser.parseFile(GameManager.MAP_FILE);
            Agent agentOne = new SimpleAgent(Player.PLAYER_ONE, new HumanRace(), sTacticHuman);
            Agent agentTwo = new SimpleAgent(Player.PLAYER_TWO, new AlienRace(), sTacticAlien);

            GameResult result = null;
            boolean fixPlayerOne = false;
            boolean fixPlayerTwo = true;

            agentOne.start(fixPlayerOne);
            agentTwo.start(fixPlayerTwo);

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
