package nl.uva.td.experiment.watchmaker;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import nl.uva.td.experiment.Score;
import nl.uva.td.game.CreepAgent;
import nl.uva.td.game.GameManager;
import nl.uva.td.game.TowerAgent;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.test.ListTowerPlacement;
import nl.uva.td.test.SpawnSimpleCreeps;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.FitnessEvaluator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.operators.IntArrayCrossover;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

public class WatchmakerExperiment implements FitnessEvaluator<int[]> {

    private static final int sGameFieldSize = 16;

    private static final String sField = "OOOOSO\n" + "OOOOXO\n" + "OOXXXO\n" + "OOXOOO\n" + "OOXXXO\n" + "OOOOEO";

    @Override
    public double getFitness(final int[] candidate, final List<? extends int[]> candidates) {
        Score score = evaluate(candidate);

        return score.getTotalTowerPoints();
    }

    @Override
    public boolean isNatural() {
        return true;
    }

    static long sParsingTime = 0;

    static long sCreationTime = 0;

    static long sRunTime = 0;

    public static Score evaluate(final int[] candidate) {
        long current = System.currentTimeMillis();

        GameField gameField = Parser.parse();
        sParsingTime += System.currentTimeMillis() - current;

        current = System.currentTimeMillis();
        CreepAgent creepAgent = new SpawnSimpleCreeps();
        List<Integer> towerPlacements = new LinkedList<Integer>();

        for (int i = 0; i < candidate.length; ++i) {
            towerPlacements.add(candidate[i]);
        }

        TowerAgent towerAgent = new ListTowerPlacement(ListTowerPlacement.generateSimpleTowerList(towerPlacements),
                towerPlacements);

        GameManager gameManager = new GameManager(creepAgent, towerAgent, gameField, false);
        sCreationTime += System.currentTimeMillis() - current;

        current = System.currentTimeMillis();
        Score result = gameManager.dryRun();
        sRunTime += System.currentTimeMillis() - current;

        return result;
    }

    public static void main(final String[] args) {

        CandidateFactory<int[]> factory = new TowerPlacement(6);

        // Create a pipeline that applies cross-over then mutation.
        List<EvolutionaryOperator<int[]>> operators = new LinkedList<EvolutionaryOperator<int[]>>();

        operators.add(new IntArrayMutation(3, sGameFieldSize));
        operators.add(new IntArrayCrossover());

        EvolutionaryOperator<int[]> pipeline = new EvolutionPipeline<int[]>(operators);

        WatchmakerExperiment fitnessEvaluator = new WatchmakerExperiment();
        SelectionStrategy<Object> selection = new RouletteWheelSelection();
        Random rng = new MersenneTwisterRNG();

        EvolutionEngine<int[]> engine = new GenerationalEvolutionEngine<int[]>(factory, pipeline, fitnessEvaluator,
                selection, rng);

        engine.addEvolutionObserver(new EvolutionObserver<int[]>() {

            @Override
            public void populationUpdate(final PopulationData<? extends int[]> data) {
                System.out.print(data.getGenerationNumber() + " -> " + data.getBestCandidateFitness() + ": \t");
                int[] best = data.getBestCandidate();
                for (int i : best) {
                    System.out.print(i + " ,");
                }

                System.out.println();
            }
        });

        // int[] result = engine.evolve(100, 10, new TargetFitness(586, true));
        int[] result = engine.evolve(100, 10, new GenerationCount(100));

        for (int i : result) {
            System.out.print(i + " ,");
        }

        System.out.println();
        System.out.println("--- TIME --- ");
        System.out.println("Parsing: " + sParsingTime / 1000 + "s");
        System.out.println("Setup: " + sCreationTime / 1000 + "s");
        System.out.println("Run: " + sRunTime / 1000 + "s");
    }
}