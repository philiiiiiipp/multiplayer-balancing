package nl.uva.td.experiment.watchmaker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.uncommons.watchmaker.framework.EvolutionaryOperator;

public class IntArrayMutation implements EvolutionaryOperator<int[]> {

    private final int mMaxMutationAmount;

    private final int mBoundary;

    public IntArrayMutation(final int maxMutationAmount, final int boundary) {
        mMaxMutationAmount = maxMutationAmount;
        mBoundary = boundary;
    }

    @Override
    public List<int[]> apply(final List<int[]> selectedCandidates, final Random rng) {
        List<int[]> result = new LinkedList<int[]>();

        for (int[] candidate : selectedCandidates) {
            int mutationCounter = rng.nextInt(mMaxMutationAmount);
            int[] mutatedCandidate = Arrays.copyOf(candidate, candidate.length);

            for (int i = 0; i < mutationCounter; ++i) {
                mutatedCandidate[rng.nextInt(mutatedCandidate.length)] = rng.nextInt(mBoundary);
            }

            result.add(mutatedCandidate);
        }

        return result;
    }

}
