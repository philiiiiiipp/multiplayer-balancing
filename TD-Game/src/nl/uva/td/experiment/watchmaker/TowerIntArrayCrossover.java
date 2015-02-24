package nl.uva.td.experiment.watchmaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.operators.IntArrayCrossover;

public class TowerIntArrayCrossover extends IntArrayCrossover {

    @Override
    protected List<int[]> mate(final int[] parent1, final int[] parent2, final int numberOfCrossoverPoints,
            final Random rng) {
        if (parent1.length != parent2.length) {
            throw new IllegalArgumentException("Cannot perform cross-over with different length parents.");
        }
        int[] offspring1 = new int[parent1.length];
        System.arraycopy(parent1, 0, offspring1, 0, parent1.length);
        int[] offspring2 = new int[parent2.length];
        System.arraycopy(parent2, 0, offspring2, 0, parent2.length);
        // Apply as many cross-overs as required.
        int[] temp = new int[parent1.length];
        for (int i = 0; i < numberOfCrossoverPoints; i++) {
            // Cross-over index is always greater than zero and less than
            // the length of the parent so that we always pick a point that
            // will result in a meaningful cross-over.
            int crossoverIndex = (1 + rng.nextInt(parent1.length - 1));
            System.arraycopy(offspring1, 0, temp, 0, crossoverIndex);
            System.arraycopy(offspring2, 0, offspring1, 0, crossoverIndex);
            System.arraycopy(temp, 0, offspring2, 0, crossoverIndex);

            for (int d = 1; d < offspring1.length; d += 2) {
                if (offspring1[d] > 1 || offspring2[d] > 1) {
                    System.out.println("ERROR!");
                }

            }
        }

        List<int[]> result = new ArrayList<int[]>(2);
        result.add(offspring1);
        result.add(offspring2);
        return result;
    }

}
