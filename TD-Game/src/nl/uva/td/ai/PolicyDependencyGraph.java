package nl.uva.td.ai;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

        for (GameInfo p : policyQuality.getBeats()) {
            enemyGraph.getPolicyQuality(p.getPlayedAgainst()).loses(new GameInfo(lastPolicy, p.getSteps()));
        }

        for (GameInfo p : policyQuality.getDraws()) {
            enemyGraph.getPolicyQuality(p.getPlayedAgainst()).draws(new GameInfo(lastPolicy, p.getSteps()));
        }

        for (GameInfo p : policyQuality.getLoses()) {
            enemyGraph.getPolicyQuality(p.getPlayedAgainst()).beats(new GameInfo(lastPolicy, p.getSteps()));
        }
    }

    public PolicyQuality getPolicyQuality(final Policy policy) {
        return mPolicyDependencies.get(policy);
    }

    public void prune(final PolicyDependencyGraph enemyGraph) {
        Map<Policy, PolicyQuality> toRemove = new HashMap<Policy, PolicyQuality>();
        Iterator<Policy> it = mPolicyDependencies.keySet().iterator();

        while (it.hasNext()) {
            Policy current = it.next();

            for (Policy p : mPolicyDependencies.keySet()) {
                if (mPolicyDependencies.get(current).compareTo(mPolicyDependencies.get(p)) == -1) {
                    toRemove.put(current, mPolicyDependencies.get(current));
                    it.remove();
                    break;
                }
            }
        }

        for (Policy p : toRemove.keySet()) {
            enemyGraph.remove(p);
        }
    }

    public void remove(final Policy policy) {
        for (Policy p : mPolicyDependencies.keySet()) {
            boolean success = false;
            success = success || mPolicyDependencies.get(p).getBeats().remove(new GameInfo(policy, 0));
            success = success || mPolicyDependencies.get(p).getDraws().remove(new GameInfo(policy, 0));
            success = success || mPolicyDependencies.get(p).getLoses().remove(new GameInfo(policy, 0));
        }
    }

    public void info() {
        for (Policy p : mPolicyDependencies.keySet()) {
            System.out.println("   ---   Dependency Graph   ---   " + p.getRace());
            System.out.println(mPolicyDependencies.get(p));
            System.out.println(p);
            System.out.println();
        }

        // Create neo4j code
        boolean created = false;
        for (Policy p : mPolicyDependencies.keySet()) {
            System.out.println("CREATE (" + p + ":Policy_" + p.getRace().getName() + " {name:\'" + p + "\'} )");

            if (!created) {
                created = true;

                for (GameInfo e : mPolicyDependencies.get(p).getBeats()) {
                    System.out.println("CREATE (" + e + ":Policy_" + e.getPlayedAgainst().getRace().getName()
                            + " {name:\'" + e + "\'} )");
                }
                for (GameInfo e : mPolicyDependencies.get(p).getDraws()) {
                    System.out.println("CREATE (" + e + ":Policy_" + e.getPlayedAgainst().getRace().getName()
                            + " {name:\'" + e + "\'} )");
                }
                for (GameInfo e : mPolicyDependencies.get(p).getLoses()) {
                    System.out.println("CREATE (" + e + ":Policy_" + e.getPlayedAgainst().getRace().getName()
                            + " {name:\'" + e + "\'} )");
                }
            }

            System.out.println("CREATE");

            Iterator<GameInfo> itBeats = mPolicyDependencies.get(p).getBeats().iterator();
            while (itBeats.hasNext()) {

                System.out.print("\t (" + p + ")-[:BEATS]->(" + itBeats.next().getPlayedAgainst() + ")");

                if (itBeats.hasNext() || mPolicyDependencies.get(p).getDraws().size() != 0
                        || mPolicyDependencies.get(p).getLoses().size() != 0) {
                    System.out.println(",");
                } else {
                    System.out.println();
                }
            }

            Iterator<GameInfo> itDraws = mPolicyDependencies.get(p).getDraws().iterator();
            while (itDraws.hasNext()) {
                System.out.print("\t (" + p + ")-[:DRAWS]->(" + itDraws.next().getPlayedAgainst() + ")");

                if (itDraws.hasNext() || mPolicyDependencies.get(p).getDraws().size() != 0) {
                    System.out.println(",");
                } else {
                    System.out.println();
                }
            }

            Iterator<GameInfo> itLoses = mPolicyDependencies.get(p).getLoses().iterator();

            while (itLoses.hasNext()) {
                System.out.print("\t (" + p + ")-[:LOSES]->(" + itLoses.next().getPlayedAgainst() + ")");

                if (itLoses.hasNext()) {
                    System.out.println(",");
                } else {
                    System.out.println();
                }
            }
        }

    }
}
