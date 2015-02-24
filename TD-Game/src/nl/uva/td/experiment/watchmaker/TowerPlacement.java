package nl.uva.td.experiment.watchmaker;

import java.util.Random;

import org.uncommons.watchmaker.framework.factories.AbstractCandidateFactory;

public class TowerPlacement extends AbstractCandidateFactory<int[]> {

    private final int mPositionCount;

    public TowerPlacement(final int positionCount) {
        mPositionCount = positionCount;
    }

    @Override
    public int[] generateRandomCandidate(final Random rng) {
        int[] candidate = new int[mPositionCount * 2];
        for (int i = 0; i < candidate.length; i += 2) {
            candidate[i] = rng.nextInt(candidate.length);
        }

        for (int i = 1; i < candidate.length; i += 2) {
            candidate[i] = rng.nextInt(2);
        }

        return candidate;
    }
}