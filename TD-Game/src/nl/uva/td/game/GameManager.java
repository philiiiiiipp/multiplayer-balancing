package nl.uva.td.game;

import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;

public class GameManager {

    private static final int STARTING_LIVES = 10;

    private static final int STARTING_GOLD = 100;

    private static final double SALARY = 20;

    private static final int SALARY_FREQUENCY = 5;

    private static final String MAP_FILE = "Standard2";

    public void run(final Agent playerOne, final Agent playerTwo) {

        PlayerAttributes playerOneAttributes = new PlayerAttributes(STARTING_LIVES, STARTING_GOLD);
        PlayerAttributes playerTwoAttributes = new PlayerAttributes(STARTING_LIVES, STARTING_GOLD);

        GameField playerOneMap = Parser.parse(MAP_FILE);
        GameField playerTwoMap = Parser.parse(MAP_FILE);

        GameState playerOneGameState = new GameState(playerOneMap, false);
        GameState playerTwoGameState = new GameState(playerTwoMap, false);

        while (playerOneAttributes.getLives() >= 0 && playerTwoAttributes.getLives() >= 0) {

            Decision playerOnesDecision = playerOne.makeDecision();
            Decision playerTwosDecision = playerTwo.makeDecision();

            playerOneGameState.step(playerOnesDecision, playerTwosDecision, playerOneAttributes);
            playerTwoGameState.step(playerTwosDecision, playerOnesDecision, playerTwoAttributes);
        }
    }

}
