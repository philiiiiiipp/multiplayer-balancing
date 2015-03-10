package nl.uva.td.game;

import nl.uva.td.ai.SimpleElementalAgent;
import nl.uva.td.ai.SimpleAlienAgent;

public class Main {

    public static void main(final String[] args) {
        GameManager gM = new GameManager();

        gM.run(new SimpleAlienAgent(), new SimpleElementalAgent());
    }
}
