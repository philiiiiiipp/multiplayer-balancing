package nl.uva.td.experiment;

import java.util.LinkedList;
import java.util.List;

import nl.uva.td.game.CreepAgent;
import nl.uva.td.game.GameManager;
import nl.uva.td.game.TowerAgent;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.test.ListTowerPlacement;
import nl.uva.td.test.SpawnSimpleCreeps;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

public class Experiment extends AbstractProblem {

    public Experiment() {
        super(3, 1);
    }

    @Override
    public Solution newSolution() {
        Solution solution = new Solution(numberOfVariables, numberOfObjectives);
        for (int variable = 0; variable < numberOfVariables; ++variable) {
            solution.setVariable(variable, EncodingUtils.newInt(0, 15));
        }

        return solution;
    }

    @Override
    public void evaluate(final Solution solution) {
        GameField gameField = Parser.parse();
        GameManager gameManager = new GameManager();

        CreepAgent creepAgent = new SpawnSimpleCreeps();
        List<Integer> towerPlacements = new LinkedList<Integer>();

        for (int i = 0; i < solution.getNumberOfVariables(); ++i) {
            towerPlacements.add(EncodingUtils.getInt(solution.getVariable(i)));
        }

        TowerAgent towerAgent = new ListTowerPlacement(ListTowerPlacement.generateSimpleTowerList(towerPlacements),
                towerPlacements);
        Score score = gameManager.run(creepAgent, towerAgent, gameField);

        solution.setObjective(0, score.getPoints());
    }
}
