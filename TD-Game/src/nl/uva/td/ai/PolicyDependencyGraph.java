package nl.uva.td.ai;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nl.uva.td.ai.PolicyQuality.Relation;

public class PolicyDependencyGraph {

    private final Map<Policy, PolicyQuality> mPolicyDependencies = new HashMap<>();

    public Set<Policy> getPolicies() {
        return new HashSet<Policy>(mPolicyDependencies.keySet());
    }

    public boolean addPolicy(final Policy lastPolicy, final PolicyQuality policyQuality,
            final PolicyDependencyGraph enemyGraph) {

        if (mPolicyDependencies.containsKey(lastPolicy)) {
            return false;
        }

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

        return true;
    }

    public PolicyQuality getPolicyQuality(final Policy policy) {
        return mPolicyDependencies.get(policy);
    }

    /**
     *
     * @param enemyGraph
     * @return true if it pruned anything, false otherwise
     */
    public boolean prune(final PolicyDependencyGraph enemyGraph) {
        Set<Policy> toRemove = new HashSet<Policy>();
        Iterator<Policy> it = mPolicyDependencies.keySet().iterator();

        while (it.hasNext()) {
            Policy current = it.next();
            boolean remove = false;

            L: for (Policy p : mPolicyDependencies.keySet()) {
                if (p == current) continue;

                Relation relation = mPolicyDependencies.get(current).compareTo(mPolicyDependencies.get(p));
                switch (relation) {
                case WORSE:
                case EQUALS:
                    remove = true;
                    break L;

                default:
                    break;
                }
            }

            if (remove) {
                toRemove.add(current);
                remove = false;
                it.remove();
            }
        }

        for (Policy p : toRemove) {
            enemyGraph.remove(p);
        }

        return !toRemove.isEmpty();
    }

    public void remove(final Policy policy) {
        for (Policy p : mPolicyDependencies.keySet()) {
            boolean success = false;
            success = success || mPolicyDependencies.get(p).getBeats().remove(new GameInfo(policy, 0));
            success = success || mPolicyDependencies.get(p).getDraws().remove(new GameInfo(policy, 0));
            success = success || mPolicyDependencies.get(p).getLoses().remove(new GameInfo(policy, 0));

            if (!success) {
                System.err.println("NONONO");
            }
        }
    }

    private static int sInfoFileNumber = 1;

    public void info() {
        // for (Policy p : mPolicyDependencies.keySet()) {
        // System.out.println("   ---   Dependency Graph   ---   " + p.getRace());
        // System.out.println(mPolicyDependencies.get(p));
        // System.out.println(p);
        // System.out.println();
        // }

        // Create neo4j code
        boolean created = false;
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("neo4jdb-" + sInfoFileNumber + ".txt", "UTF-8");
        } catch (Exception e1) {}

        for (Policy p : mPolicyDependencies.keySet()) {
            writer.println("CREATE (" + p + ":Policy_" + p.getRace().getName() + " {name:\'" + p + "\'} )");

            if (!created) {
                created = true;

                for (GameInfo e : mPolicyDependencies.get(p).getBeats()) {
                    writer.println("CREATE (" + e + ":Policy_" + e.getPlayedAgainst().getRace().getName() + " {name:\'"
                            + e + "\'} )");
                }
                for (GameInfo e : mPolicyDependencies.get(p).getDraws()) {
                    writer.println("CREATE (" + e + ":Policy_" + e.getPlayedAgainst().getRace().getName() + " {name:\'"
                            + e + "\'} )");
                }
                for (GameInfo e : mPolicyDependencies.get(p).getLoses()) {
                    writer.println("CREATE (" + e + ":Policy_" + e.getPlayedAgainst().getRace().getName() + " {name:\'"
                            + e + "\'} )");
                }
            }

            writer.println("CREATE");

            Iterator<GameInfo> itBeats = mPolicyDependencies.get(p).getBeats().iterator();
            while (itBeats.hasNext()) {

                writer.print("\t (" + p + ")-[:BEATS]->(" + itBeats.next().getPlayedAgainst() + ")");

                if (itBeats.hasNext() || mPolicyDependencies.get(p).getDraws().size() != 0
                        || mPolicyDependencies.get(p).getLoses().size() != 0) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }

            Iterator<GameInfo> itDraws = mPolicyDependencies.get(p).getDraws().iterator();
            while (itDraws.hasNext()) {
                writer.print("\t (" + p + ")-[:DRAWS]->(" + itDraws.next().getPlayedAgainst() + ")");

                if (itDraws.hasNext() || mPolicyDependencies.get(p).getLoses().size() != 0) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }

            Iterator<GameInfo> itLoses = mPolicyDependencies.get(p).getLoses().iterator();

            while (itLoses.hasNext()) {
                writer.print("\t (" + p + ")-[:LOSES]->(" + itLoses.next().getPlayedAgainst() + ")");

                if (itLoses.hasNext()) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
        }
        writer.close();
    }

    public int test(final PolicyDependencyGraph enemy) {
        int s = -1;
        int total = 0;

        for (Policy p : mPolicyDependencies.keySet()) {
            PolicyQuality d = mPolicyDependencies.get(p);
            int size = d.getBeats().size() + d.getDraws().size() + d.getLoses().size();

            if (s == -1) {
                s = size;
            }

            if (s != size) {
                System.out.println("err");

                for (GameInfo q : d.getBeats()) {
                    if (!d.getDraws().contains(q)) {
                        System.out.println(q);
                    }

                    if (!d.getLoses().contains(q)) {
                        System.out.println(q.getPlayedAgainst());
                    }
                }

                for (GameInfo q : d.getDraws()) {
                    if (!enemy.getPolicies().contains(q.getPlayedAgainst())) {
                        System.out.println(q.getPlayedAgainst());
                    }
                }

                for (GameInfo q : d.getLoses()) {
                    if (!enemy.getPolicies().contains(q.getPlayedAgainst())) {
                        System.out.println(q.getPlayedAgainst());
                    }
                }

            }

            total += size;
        }

        return total;
    }
}
