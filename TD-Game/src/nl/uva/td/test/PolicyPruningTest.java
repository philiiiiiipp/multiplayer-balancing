package nl.uva.td.test;

import java.util.Arrays;

import nl.uva.td.ai.GameInfo;
import nl.uva.td.ai.Policy;
import nl.uva.td.ai.PolicyDependencyGraph;
import nl.uva.td.ai.PolicyQuality;
import nl.uva.td.game.faction.alien.AlienRace;
import nl.uva.td.game.faction.human.HumanRace;

public class PolicyPruningTest {

    public static void main(final String[] args) {

        PolicyDependencyGraph graphOne = new PolicyDependencyGraph();
        PolicyDependencyGraph graphTwo = new PolicyDependencyGraph();

        Policy a339 = new Policy(Arrays.asList(1), new AlienRace());
        Policy a338 = new Policy(Arrays.asList(2), new AlienRace());

        Policy h337 = new Policy(Arrays.asList(1), new HumanRace());
        Policy h340 = new Policy(Arrays.asList(2), new HumanRace());

        PolicyQuality a339Quality = new PolicyQuality();
        // a339Quality.draws(new GameInfo(h340, 10));
        // a339Quality.beats(new GameInfo(h337, 10));

        PolicyQuality h337Quality = new PolicyQuality();
        h337Quality.loses(new GameInfo(a339, 10));
        // h337Quality.draws(new GameInfo(a338, 10));

        PolicyQuality a338Quality = new PolicyQuality();
        a338Quality.draws(new GameInfo(h337, 10));
        // a338Quality.loses(new GameInfo(h340, 10));

        PolicyQuality h340Quality = new PolicyQuality();
        h340Quality.draws(new GameInfo(a339, 10));
        h340Quality.beats(new GameInfo(a338, 10));

        graphOne.addPolicy(a339, a339Quality, graphTwo);
        graphTwo.addPolicy(h337, h337Quality, graphOne);

        graphOne.addPolicy(a338, a338Quality, graphTwo);
        graphTwo.addPolicy(h340, h340Quality, graphOne);

        Policy a3392 = new Policy(Arrays.asList(1), new AlienRace());
        PolicyQuality a3392Quality = new PolicyQuality();
        a3392Quality.draws(new GameInfo(h340, 10));
        a3392Quality.beats(new GameInfo(h337, 10));

        graphOne.addPolicy(a3392, a3392Quality, graphTwo);

        graphOne.prune(graphTwo);
        graphTwo.prune(graphOne);

        graphTwo.info();

    }
}
