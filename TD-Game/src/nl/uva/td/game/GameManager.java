package nl.uva.td.game;

import nl.uva.td.ai.Agent;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.visual.TDGameWrapper;

public class GameManager {

    public enum Player {
        PLAYER_ONE,
        NONE,
        PLAYER_TWO
    }

    public static final int STARTING_LIVES = 10;

    public static final int STARTING_GOLD = 100;

    public static final double BASE_SALARY = 20;

    public static final int SALARY_FREQUENCY = 5;

    public static final String MAP_FILE = "Standard4";

    private static final boolean SHOW_UI = false;

    private static long sParsingTime = 0;

    private static long sDecisionTime = 0;

    private static long sStepTime = 0;

    private static long sTimer = 0;

    public static Player run(final Agent playerOne, final Agent playerTwo, final boolean fixPlayerOne,
            final boolean fixPlayerTwo) {
        PlayerAttributes playerOneAttributes = new PlayerAttributes(STARTING_LIVES);
        PlayerAttributes playerTwoAttributes = new PlayerAttributes(STARTING_LIVES);

        startTimer();
        GameField playerOneMap = Parser.parseFile(MAP_FILE);
        GameField playerTwoMap = Parser.parseFile(MAP_FILE);
        sParsingTime += stopTimer();

        if (SHOW_UI) {
            new TDGameWrapper().start();
        }

        GameState playerOneGameState = new GameState(playerOneMap, SHOW_UI, playerOne.getName());
        GameState playerTwoGameState = new GameState(playerTwoMap, SHOW_UI, playerTwo.getName());

        int step = 0;
        while (step < 200 && playerOneAttributes.getLives() >= 0 && playerTwoAttributes.getLives() >= 0) {

            startTimer();
            Decision playerOnesDecision = playerOne.makeDecision(playerOneMap, playerTwoMap, playerOneAttributes,
                    playerTwoAttributes, step, playerTwo, fixPlayerOne);
            Decision playerTwosDecision = playerTwo.makeDecision(playerTwoMap, playerOneMap, playerTwoAttributes,
                    playerOneAttributes, step, playerOne, fixPlayerTwo);

            sDecisionTime += stopTimer();

            startTimer();
            playerOneGameState.step(playerOnesDecision, playerTwosDecision, playerOneAttributes, playerTwoAttributes);
            playerTwoGameState.step(playerTwosDecision, playerOnesDecision, playerTwoAttributes, playerOneAttributes);
            sStepTime += stopTimer();
            step++;
        }

        if (playerOneAttributes.getLives() <= 0 && playerTwoAttributes.getLives() <= 0) {
            return Player.NONE;
        } else if (playerOneAttributes.getLives() <= 0) {
            return playerTwo.getPlayer();
        } else if (playerTwoAttributes.getLives() <= 0) {

            System.out.println("Creep Stats");
            System.out.println(playerOne.getPlayer() + " " + playerOne.getRace());
            printCreepStats(playerOneAttributes);
            System.out.println("----");

            System.out.println(playerTwo.getPlayer() + " " + playerTwo.getRace());
            printCreepStats(playerTwoAttributes);
            return playerOne.getPlayer();
        } else {
            // More than 100step -> Draw
            return Player.NONE;
        }
    }

    private static void startTimer() {
        sTimer = System.currentTimeMillis();
    }

    private static long stopTimer() {
        long result = System.currentTimeMillis() - sTimer;
        sTimer = 0;

        return result;
    }

    public static void printCreepStats(final PlayerAttributes attributes) {
        System.out.println("Health: " + attributes.getHealth() + " Movement: " + attributes.getMovement() + " Amount: "
                + attributes.getAmountOfCreeps());
    }

    public static void printStatistics() {
        System.out.println("-------- STATISTICS ----------");
        System.out.println("Parsing time: " + sParsingTime / 1000 + "s");
        System.out.println("Decision time: " + sDecisionTime / 1000 + "s");
        System.out.println("Step time: " + sStepTime / 1000 + "s");
    }

    public static void resetStatistics() {
        sDecisionTime = 0;
        sParsingTime = 0;
        sStepTime = 0;
    }
}
