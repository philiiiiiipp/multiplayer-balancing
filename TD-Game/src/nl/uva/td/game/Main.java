package nl.uva.td.game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import nl.uva.td.experiment.Experiment;
import nl.uva.td.experiment.Score;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.test.ListTowerPlacement;
import nl.uva.td.test.SpawnSimpleCreeps;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;

public class Main {

    public static void main(final String[] args) {
        BF();
    }

    public static void TEST() {
        GameField gameField = Parser.parse();

        CreepAgent creepAgent = new SpawnSimpleCreeps();
        List<Integer> towerPlacements = new LinkedList<Integer>();

        TowerAgent towerAgent = new ListTowerPlacement(ListTowerPlacement.generateSimpleTowerList(towerPlacements),
                towerPlacements);

        GameManager gameManager = new GameManager(creepAgent, towerAgent, gameField, true);
        Score score = gameManager.dryRun();
    }

    public static void AI() {
        NondominatedPopulation result = new Executor().withProblemClass(Experiment.class).withAlgorithm("NSGAII")
                .withMaxEvaluations(10000).distributeOnAllCores().run();

        int solutionCounter = 0;
        for (Solution s : result) {
            System.out.print(++solutionCounter + ": " + s.getObjective(0) + " ");
            for (int variable = 0; variable < s.getNumberOfVariables(); ++variable) {
                System.out.print(EncodingUtils.getInt(s.getVariable(variable)) + ", ");
            }
            System.out.println();
        }
    }

    public static void BF() {
        double best = 0;
        List<Integer> bestList = new LinkedList<Integer>();

        best = bruteForce(new boolean[15], new LinkedList<Integer>(), 0,
                new LinkedList<Integer>(Collections.nCopies(15, -1)), 15);
        System.out.println(best);

        for (Integer i : bestList) {
            System.out.print(i + ", ");
        }

    }

    public static double bruteForce(final boolean[] takenNumbers, final List<Integer> towers, double bestValue,
            final List<Integer> bestTowers, final int desiredTowerNumber) {
        if (towers.size() == desiredTowerNumber) {
            // evaluate
            GameField gameField = Parser.parse();

            CreepAgent creepAgent = new SpawnSimpleCreeps();

            TowerAgent towerAgent = new ListTowerPlacement(ListTowerPlacement.generateSimpleTowerList(towers), towers);

            GameManager gameManager = new GameManager(creepAgent, towerAgent, gameField, false);
            Score score = gameManager.dryRun();

            if (score.getTotalTowerPoints() > bestValue) {

                for (Integer i : towers) {
                    System.out.print(i + ", ");
                }

                System.out.println(score.getTotalTowerPoints());
                bestValue = score.getTotalTowerPoints();
            }
            for (Integer i : towers) {
                System.out.print(i + ", ");
            }

            System.out.println(score.getTotalTowerPoints() + "  " + score.getSteps());
            return bestValue;
        }

        for (int towerToAdd = 0; towerToAdd < desiredTowerNumber; ++towerToAdd) {
            if (towers.size() == 0) {
                System.out.println(towerToAdd);
            }

            if (takenNumbers[towerToAdd]) {
                continue;
            }

            takenNumbers[towerToAdd] = true;

            towers.add(towerToAdd);
            bestValue = bruteForce(takenNumbers, towers, bestValue, bestTowers, desiredTowerNumber);
            towers.remove(towers.size() - 1);
            takenNumbers[towerToAdd] = false;

            if (bestValue < 20 && towers.size() != 0) {
                break;
            }
        }

        return bestValue;
    }
}
