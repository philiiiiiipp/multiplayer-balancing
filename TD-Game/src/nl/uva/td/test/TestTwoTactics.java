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

    private static String sTacticAlien = "4;8;12;";

    private static String sTacticHuman = "4;8;12;";

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
