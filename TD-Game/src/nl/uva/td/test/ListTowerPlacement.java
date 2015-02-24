package nl.uva.td.test;

import java.util.LinkedList;
import java.util.List;

import nl.uva.td.game.TowerAgent;
import nl.uva.td.game.tower.FireTower;
import nl.uva.td.game.tower.IceTower;
import nl.uva.td.game.tower.SimpleTower;
import nl.uva.td.game.tower.Tower;
import nl.uva.td.game.tower.Tower.Type;

public class ListTowerPlacement extends TowerAgent {

    private final List<Tower> mTowersToPlace;

    private final List<Integer> mPositions;

    public ListTowerPlacement(final List<Tower> towersToPlace, final List<Integer> positions) {
        mTowersToPlace = towersToPlace;
        mPositions = positions;
    }

    @Override
    public Tower nextTower(final int stepCounter) {
        if (mTowersToPlace.size() > stepCounter) {
            return mTowersToPlace.get(stepCounter);
        } else {
            return null;
        }
    }

    @Override
    public int nextTowerPosition(final int stepCounter) {
        if (mPositions.size() > stepCounter) {
            return mPositions.get(stepCounter);
        } else {
            return -1;
        }
    }

    /**
     * Fills a list with positionList.size() simple tower elements
     *
     * @param positionList
     *            The amount of towers to add
     * @return A list of SimpleTower elements
     */
    public static List<Tower> generateSimpleTowerList(final List<Integer> positionList) {
        List<Tower> towerList = new LinkedList<Tower>();
        for (int position = 0; position < positionList.size(); ++position) {
            towerList.add(new SimpleTower());
        }

        return towerList;
    }

    public static List<Tower> generateAdvancedTowerList(final int[] positionList) {
        List<Tower> towerList = new LinkedList<Tower>();
        for (int position = 1; position < positionList.length; position += 2) {
            Type current = Type.values()[positionList[position]];

            switch (current) {
            case ICE:
                towerList.add(new IceTower());
                break;
            case FIRE:
                towerList.add(new FireTower());
                break;

            default:
                throw new RuntimeException("Could not parse! Wrong tower type? Look in Tower.Type");
            }
        }

        return towerList;
    }

    public static List<Integer> generateAdvancedPlacesList(final int[] positionList) {
        List<Integer> placesList = new LinkedList<Integer>();
        for (int position = 0; position < positionList.length; position += 2) {
            placesList.add(positionList[position]);
        }

        return placesList;
    }
}
