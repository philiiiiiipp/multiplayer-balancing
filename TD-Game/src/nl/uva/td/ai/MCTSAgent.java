package nl.uva.td.ai;

import java.util.ArrayList;
import java.util.List;

import nl.uva.td.game.GameManager;
import nl.uva.td.game.PlayerAttributes;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.map.CreepField;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.TowerField;

public class MCTSAgent extends Agent {

    List<Decision[]> mPastDecisions = new ArrayList<Decision[]>();

    Decision[] mNextDecision = new Decision[GameManager.SALARY_FREQUENCY];

    public MCTSAgent() {
        super("MCTS Agent");
    }

    @Override
    public Decision makeDecision(final GameField yourMap, final GameField enemyMap,
            final PlayerAttributes yourAttributes, final PlayerAttributes enemyAttributes, final int elapsedSteps,
            final Agent enemyAgent) {

        return new Decision();
    }

    private double predict(final Decision decision) {

        return 0;
    }

    private double creepScore(final GameField gameField) {
        double score = 0;

        for (CreepField creepField : gameField.getCreepFields()) {
            score += creepField.getScore() * creepField.getDistanceFromStart() / gameField.getCreepFields().size();
        }

        return score;
    }

    private double towerScore(final GameField gameField) {
        double score = 0;

        for (TowerField tF : gameField.getTowerFields()) {
            if (tF.getTower() != null) {
                score++;
            }
        }

        return score;
    }
}
