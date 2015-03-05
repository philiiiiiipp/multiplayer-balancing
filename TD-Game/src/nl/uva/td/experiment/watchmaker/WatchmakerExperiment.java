package nl.uva.td.experiment.watchmaker;

import java.util.List;

import nl.uva.td.experiment.Score;
import nl.uva.td.game.CreepAgent;
import nl.uva.td.game.GameState;
import nl.uva.td.game.TowerAgent;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.game.tower.Tower;
import nl.uva.td.test.ListTowerPlacement;

import org.uncommons.watchmaker.framework.FitnessEvaluator;

public class WatchmakerExperiment implements FitnessEvaluator<int[]> {

    /** The total time spend for game field parsing */
    private static long sParsingTime = 0;

    /** The total time spend for game field creation */
    private static long sCreationTime = 0;

    /** The total time spend for running the game */
    private static long sRunTime = 0;

    /** The game field */
    private final String mGameField;

    /** The creeps send on this game field */
    private final CreepAgent mCreepAgent;

    public WatchmakerExperiment(final CreepAgent creepAgent, final String gameField) {
        mCreepAgent = creepAgent;
        mGameField = gameField;
    }

    @Override
    public double getFitness(final int[] candidate, final List<? extends int[]> candidates) {
        Score score = evaluate(candidate);

        return score.getTotalTowerPoints();
    }

    @Override
    public boolean isNatural() {
        return true;
    }

    /**
     * Evaluates one tower placement candidate
     *
     * @param candidate
     *            The candidate to evaluate
     * @return The score of the candidate
     */
    private Score evaluate(final int[] candidate) {
        long current = System.currentTimeMillis();

        GameField gameField = Parser.parseFile(mGameField);
        sParsingTime += System.currentTimeMillis() - current;

        current = System.currentTimeMillis();

        List<Integer> towerPlacements = ListTowerPlacement.generateAdvancedPlacesList(candidate);
        List<Tower> towerTypes = ListTowerPlacement.generateAdvancedTowerList(candidate);

        TowerAgent towerAgent = new ListTowerPlacement(towerTypes, towerPlacements);

        GameState gameManager = null; // new GameState(mCreepAgent, towerAgent, gameField, false);
        sCreationTime += System.currentTimeMillis() - current;

        current = System.currentTimeMillis();
        Score result = gameManager.dryRun();
        sRunTime += System.currentTimeMillis() - current;

        return result;
    }

    /**
     * Prints all the statistics gathered
     */
    public static void printTimeStatistic() {
        System.out.println();
        System.out.println("--- TIME --- ");
        System.out.println("Parsing: " + sParsingTime / 1000 + "s");
        System.out.println("Setup: " + sCreationTime / 1000 + "s");
        System.out.println("Run: " + sRunTime / 1000 + "s");
        System.out.println();
    }

}
