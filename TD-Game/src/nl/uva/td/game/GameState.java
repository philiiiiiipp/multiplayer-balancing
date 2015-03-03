package nl.uva.td.game;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import nl.uva.td.experiment.Score;
import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.agent.TowerPlacement;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.tower.Tower;
import nl.uva.td.game.unit.Creep;

public class GameState extends GameUpdateHUB {

    /** The amount of lives a player starts with */
    public final static int PLAYER_STARTING_LIVES = 10;

    private final static int TOWER_PLACEMENT_FREQUENCY = 15;

    private final CreepAgent mCreepAgent = null;
    private final TowerAgent mTowerAgent = null;

    private final List<Tower> mTowerList = new LinkedList<Tower>();
    private final HashSet<Creep> mCreeps = new HashSet<Creep>();

    public GameState(final GameField gameField, final boolean showUI) {
        super(gameField, showUI);
    }

    public void step(final Decision myDecision, final Decision enemyDecision, final PlayerAttributes myAttributes) {
        // Place towers first
        for (TowerPlacement towerToPlace : myDecision.wantsToPlaceTowers()) {

            if (mGameField.addTowerToTheGame(towerToPlace.getTower(), towerToPlace.getTowerPosition())) {
                if (myAttributes.getGold() >= towerToPlace.getTower().getCost()) {
                    mTowerList.add(towerToPlace.getTower());
                } else {
                    System.err.println("You dont have the cash!");
                }
            } else {
                System.err.println("Tried to place tower where a tower already exists?");
            }
        }

        // Place creep
        for (Creep creepToPlace : enemyDecision.wantsToPlaceCreeps()) {
            mGameField.addCreepToTheGame(creepToPlace);
            mCreeps.add(creepToPlace);
        }

        // Shoot
        for (Tower tower : mTowerList) {
            Set<Creep> killedCreep = tower.shoot();

            if (killedCreep != null) {
                mCreeps.removeAll(killedCreep);
            }
        }

        // Walk
        Iterator<Creep> creepIterator = mCreeps.iterator();
        while (creepIterator.hasNext()) {
            Creep current = creepIterator.next();

            if (current.move()) {
                // creep went into the goal
                myAttributes.setLives(myAttributes.getLives() - 1);
                creepIterator.remove();
            }
        }
    }

    public Score dryRun() {
        int playerTotalHealth = PLAYER_STARTING_LIVES;
        int lastStepLivesLost = 0;
        int stepCounter = 0;
        int totalTowerPoints = 0;
        int lastStepTowerPoints = 0;
        Set<Creep> creeps = new HashSet<Creep>();
        Set<Tower> towers = new HashSet<Tower>();
        Set<Creep> killedCreep = null;

        /*
         * 1.Place 2.Shoot 3.Walk
         */
        while (playerTotalHealth > 0) {

            // Place towers first
            if (stepCounter % TOWER_PLACEMENT_FREQUENCY == 0) {
                Tower nextTower = mTowerAgent.nextTower(stepCounter / TOWER_PLACEMENT_FREQUENCY);
                if (nextTower != null) {
                    int nextTowerPosition = mTowerAgent.nextTowerPosition(stepCounter / TOWER_PLACEMENT_FREQUENCY);
                    if (mGameField.addTowerToTheGame(nextTower, nextTowerPosition)) {
                        towers.add(nextTower);
                    }
                }
            }

            super.updateUI(new Score(stepCounter, lastStepTowerPoints, totalTowerPoints, lastStepLivesLost,
                    playerTotalHealth));

            // Place creep
            Creep nextCreep = mCreepAgent.nextCreep(stepCounter);
            if (nextCreep != null) {
                mGameField.addCreepToTheGame(nextCreep);
                creeps.add(nextCreep);
            }

            super.updateUI(new Score(stepCounter, lastStepTowerPoints, totalTowerPoints, lastStepLivesLost,
                    playerTotalHealth));

            // Shoot
            for (Tower tower : towers) {
                killedCreep = tower.shoot();

                if (killedCreep != null) {
                    creeps.removeAll(killedCreep);

                    // update points
                    for (Creep creep : killedCreep) {
                        lastStepTowerPoints += 1; // creep.getMaxHealth();
                    }

                    killedCreep = null;
                }
            }

            super.updateUI(new Score(stepCounter, 0, totalTowerPoints, lastStepLivesLost, playerTotalHealth));

            totalTowerPoints += lastStepTowerPoints;

            // Walk
            Iterator<Creep> creepIterator = creeps.iterator();
            while (creepIterator.hasNext()) {
                Creep current = creepIterator.next();

                if (current.move()) {
                    // creep went into the goal
                    playerTotalHealth--;
                    lastStepLivesLost++;
                    creepIterator.remove();
                }
            }

            super.updateUI(new Score(++stepCounter, lastStepTowerPoints, totalTowerPoints, lastStepLivesLost,
                    playerTotalHealth));
            lastStepTowerPoints = 0;
            lastStepLivesLost = 0;
        }

        return new Score(++stepCounter, lastStepTowerPoints, totalTowerPoints, lastStepLivesLost, playerTotalHealth);
    }

    @Override
    public void run() {
        dryRun();
    }

    private static void printField(final GameField gameField) {
        for (int x = 0; x < gameField.getGameField().length; ++x) {
            for (int y = 0; y < gameField.getGameField()[x].length; ++y) {
                System.out.print(gameField.getGameField()[x][y]);
            }
            System.out.println();
        }
        System.out.println();
    }
}