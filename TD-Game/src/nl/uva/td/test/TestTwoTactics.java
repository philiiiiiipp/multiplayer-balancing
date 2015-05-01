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

    private static String sTacticAlien = "4;1;3;1;0;1;1;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;3;0;0;0;";

    private static String sTacticHuman = "5;3;3;3;3;3;3;3;3;3;3;0;0;0;0;0;0;0;0;0;0;0;0";

    public static void main(final String[] args) {
        GameField playerOneMap = Parser.parseFile(GameManager.MAP_FILE);
        Agent agentOne = new SimpleAgent(Player.PLAYER_ONE, new HumanRace(), sTacticHuman);
        Agent agentTwo = new SimpleAgent(Player.PLAYER_TWO, new AlienRace(), sTacticAlien);

        GameResult result = null;
        boolean fixPlayerOne = false;
        boolean fixPlayerTwo = true;

        agentOne.start();
        agentTwo.start();

        result = GameManager.run(agentOne, agentTwo, fixPlayerOne, fixPlayerTwo);

        System.out.println(result.getSteps() + " " + result.getWinner() + " ");
    }

}
