package nl.uva.td.game;

import nl.uva.td.experiment.Experiment;

import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;

public class Main {

    public static void main(final String[] args) {
        NondominatedPopulation result = new Executor().withProblemClass(Experiment.class).withAlgorithm("NSGAII")
                .withMaxEvaluations(10000).distributeOnAllCores().run();

        int solutionCounter = 0;
        for (Solution s : result) {
            System.out.print(++solutionCounter + ": " + s.getObjective(0) + " ");
            for (int variable = 0; variable < s.getNumberOfVariables(); ++variable) {
                System.out.print(s.getVariable(variable) + ", ");
            }
            System.out.println();
        }
    }
}
