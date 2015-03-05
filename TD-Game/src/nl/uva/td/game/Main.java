package nl.uva.td.game;

import nl.uva.td.ai.SimpleAgent;

public class Main {

    public static void main(final String[] args) {
        GameManager gM = new GameManager();

        gM.run(new SimpleAgent(), new SimpleAgent());
    }
}
