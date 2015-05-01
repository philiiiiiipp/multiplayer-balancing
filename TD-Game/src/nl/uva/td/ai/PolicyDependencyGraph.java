package nl.uva.td.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PolicyDependencyGraph {

    private final Map<Policy, PolicyQuality> mPolicyDependencies = new HashMap<>();

    public Set<Policy> getPolicies() {
        return new HashSet<Policy>(mPolicyDependencies.keySet());
    }

    public void addPolicy(final Policy lastPolicy, final PolicyQuality policyQuality,
            final PolicyDependencyGraph enemyGraph) {
        mPolicyDependencies.put(lastPolicy, policyQuality);

        for (Policy p : policyQuality.getBeats()) {
            enemyGraph.getPolicyQuality(p).loses(lastPolicy);
        }

        for (Policy p : policyQuality.getDraws()) {
            enemyGraph.getPolicyQuality(p).draws(lastPolicy);
        }

        for (Policy p : policyQuality.getLoses()) {
            enemyGraph.getPolicyQuality(p).beats(lastPolicy);
        }
    }

    public PolicyQuality getPolicyQuality(final Policy policy) {
        return mPolicyDependencies.get(policy);
    }

    public void info() {
        for (Policy p : mPolicyDependencies.keySet()) {
            System.out.println("   ---   Dependency Graph   ---   " + p.getRace());
            System.out.println(mPolicyDependencies.get(p));
            System.out.println(p);
            System.out.println();
        }
    }

}