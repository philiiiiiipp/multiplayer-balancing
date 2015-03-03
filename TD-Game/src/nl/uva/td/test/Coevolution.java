package nl.uva.td.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import nl.uva.td.experiment.watchmaker.IntArrayMutation;
import nl.uva.td.experiment.watchmaker.TowerIntArrayCrossover;
import nl.uva.td.experiment.watchmaker.TowerPlacementCandidate;
import nl.uva.td.experiment.watchmaker.WatchmakerExperiment;
import nl.uva.td.game.CreepAgent;
import nl.uva.td.game.TowerAgent;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.game.tower.FireTower;
import nl.uva.td.game.tower.IceTower;
import nl.uva.td.game.tower.Tower;
import nl.uva.td.game.unit.Creep;
import nl.uva.td.game.unit.FireCreep;
import nl.uva.td.game.unit.IceCreep;

import org.uncommons.maths.random.MersenneTwisterRNG;
import org.uncommons.watchmaker.framework.CandidateFactory;
import org.uncommons.watchmaker.framework.EvolutionEngine;
import org.uncommons.watchmaker.framework.EvolutionObserver;
import org.uncommons.watchmaker.framework.EvolutionaryOperator;
import org.uncommons.watchmaker.framework.GenerationalEvolutionEngine;
import org.uncommons.watchmaker.framework.PopulationData;
import org.uncommons.watchmaker.framework.SelectionStrategy;
import org.uncommons.watchmaker.framework.operators.EvolutionPipeline;
import org.uncommons.watchmaker.framework.selection.RouletteWheelSelection;
import org.uncommons.watchmaker.framework.termination.GenerationCount;

public class Coevolution {

    public static void main(final String[] args) {

        String fieldName = "Standard3";

        TowerAgent towerAgent = evolveTowers(fieldName, new SpawnCreeps());
        // CreepAgent creepAgent = evolveCreeps(fieldName, towerAgent);
    }

    private static CreepAgent evolveCreeps(final String fieldName, final CreepAgent creepAgent) {
        GameField gameField = Parser.parseFile(fieldName);

        CandidateFactory<int[]> factory = new TowerPlacementCandidate(10);

        // Create a pipeline that applies cross-over then mutation.
        List<EvolutionaryOperator<int[]>> operators = new LinkedList<EvolutionaryOperator<int[]>>();

        operators.add(new IntArrayMutation(3, gameField.getTowerFields().size()));
        operators.add(new TowerIntArrayCrossover());

        EvolutionaryOperator<int[]> pipeline = new EvolutionPipeline<int[]>(operators);

        WatchmakerExperiment fitnessEvaluator = new WatchmakerExperiment(creepAgent, fieldName);
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
        final int[] result = engine.evolve(1000, 10, new GenerationCount(100));

        for (int i : result) {
            System.out.print(i + " ,");
        }

        return new CreepAgent() {

            @Override
            public Creep nextCreep(final int stepCounter) {
                Tower.Type towerType = Tower.Type.values()[result[stepCounter]];

                switch (towerType) {
                case FIRE:
                    return new FireCreep(3 + stepCounter / 10);
                case ICE:
                    return new IceCreep(3 + stepCounter / 10);

                default:
                    throw new RuntimeException("Could not find the tower type");
                }
            }
        };
    }

    private static TowerAgent evolveTowers(final String fieldName, final CreepAgent creepAgent) {

        GameField gameField = Parser.parseFile(fieldName);

        CandidateFactory<int[]> factory = new TowerPlacementCandidate(10);

        // Create a pipeline that applies cross-over then mutation.
        List<EvolutionaryOperator<int[]>> operators = new LinkedList<EvolutionaryOperator<int[]>>();

        operators.add(new IntArrayMutation(3, gameField.getTowerFields().size()));
        operators.add(new TowerIntArrayCrossover());

        EvolutionaryOperator<int[]> pipeline = new EvolutionPipeline<int[]>(operators);

        WatchmakerExperiment fitnessEvaluator = new WatchmakerExperiment(creepAgent, fieldName);
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
        final int[] result = engine.evolve(1000, 10, new GenerationCount(100));

        for (int i : result) {
            System.out.print(i + " ,");
        }

        return new TowerAgent() {

            @Override
            public int nextTowerPosition(final int stepCounter) {
                return result[stepCounter * 2];
            }

            @Override
            public Tower nextTower(final int stepCounter) {
                Tower.Type towerType = Tower.Type.values()[result[stepCounter * 2 + 1]];

                switch (towerType) {
                case FIRE:
                    return new FireTower();
                case ICE:
                    return new IceTower();

                default:
                    throw new RuntimeException("Could not find the tower type");
                }
            }
        };
    }

}
