package nl.uva.td.game;

import nl.uva.td.ai.Agent;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.game.unit.Creep;
import nl.uva.td.visual.TDGameWrapper;

public class GameManager {

    private static final int STARTING_LIVES = 10;

    private static final int STARTING_GOLD = 100;

    private static final double SALARY = 20;

    private static final int SALARY_FREQUENCY = 5;

    private static final String MAP_FILE = "Standard2";

    private static final boolean SHOW_UI = true;

    public void run(final Agent playerOne, final Agent playerTwo) {

        PlayerAttributes playerOneAttributes = new PlayerAttributes(STARTING_LIVES, STARTING_GOLD);
        PlayerAttributes playerTwoAttributes = new PlayerAttributes(STARTING_LIVES, STARTING_GOLD);

        GameField playerOneMap = Parser.parseFile(MAP_FILE);
        GameField playerTwoMap = Parser.parseFile(MAP_FILE);

        if (SHOW_UI) {
            new TDGameWrapper().start();
        }

        GameState playerOneGameState = new GameState(playerOneMap, SHOW_UI);
        GameState playerTwoGameState = new GameState(playerTwoMap, SHOW_UI);

        int additionalSalaryPlayerOne = 0;
        int additionalSalaryPlayerTwo = 0;
        int step = 0;
        while (playerOneAttributes.getLives() >= 0 && playerTwoAttributes.getLives() >= 0) {

            Decision playerOnesDecision = playerOne.makeDecision(playerOneMap, playerTwoMap, playerOneAttributes,
                    playerTwoAttributes, step);
            Decision playerTwosDecision = playerTwo.makeDecision(playerTwoMap, playerOneMap, playerTwoAttributes,
                    playerOneAttributes, step);

            StepResult stepResultPlayerOne = playerOneGameState.step(playerOnesDecision, playerTwosDecision,
                    playerOneAttributes, playerTwoAttributes);
            StepResult stepResultPlayerTwo = playerTwoGameState.step(playerTwosDecision, playerOnesDecision,
                    playerTwoAttributes, playerOneAttributes);

            System.out.println(playerOneAttributes.getGold());

            additionalSalaryPlayerOne += stepResultPlayerTwo.getExtraSalary();
            additionalSalaryPlayerTwo += stepResultPlayerOne.getExtraSalary();

            if (step++ != 0 && step % SALARY_FREQUENCY == 0) {
                playerOneAttributes.setGold(playerOneAttributes.getGold() + SALARY + additionalSalaryPlayerOne);
                playerTwoAttributes.setGold(playerTwoAttributes.getGold() + SALARY + additionalSalaryPlayerTwo);
            }
        }
    }

    private double calculateSalaryIncrease(final Decision decision) {
        double salaryIncrease = 0;
        for (Creep creep : decision.wantsToPlaceCreeps()) {
            salaryIncrease += creep.getSalaryIncrease();
        }

        return salaryIncrease;
    }

}
