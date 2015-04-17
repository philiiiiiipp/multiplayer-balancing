package nl.uva.td.game;

import nl.uva.td.ai.SimpleAlienAgent;
import nl.uva.td.ai.SimpleHumanAgent;
import nl.uva.td.game.GameManager.Player;

public class Main {

    public static void main(final String[] args) {
        GameManager.run(new SimpleAlienAgent(Player.PLAYER_ONE), new SimpleHumanAgent(Player.PLAYER_TWO), false, false);
    }
}
