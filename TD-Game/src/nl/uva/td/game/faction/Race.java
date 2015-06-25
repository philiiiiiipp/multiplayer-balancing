package nl.uva.td.game.faction;

import nl.uva.td.game.faction.alien.AlienRace;
import nl.uva.td.game.faction.human.HumanRace;
import nl.uva.td.game.faction.tower.Tower;

public interface Race {

    public enum Type {
        HUMAN,
        ALIEN
    }

    /**
     * Returns an initialised tower for the given id number
     *
     * @param number
     *            The id number of the tower
     * @return An initialised tower
     */
    public Tower getTowerByNumber(int number);

    /**
     * Get the available amount of towers from this race
     *
     * @return The available amount of towers from this race
     */
    public int getAvailableTowerAmount();

    /**
     * The name of this race
     *
     * @return The name of this race
     */
    public String getName();

    /**
     * The type of this race
     *
     * @return The type of this race
     */
    public Race.Type getType();

    public static Race getRaceForType(final Race.Type type) {
        if (type == Type.HUMAN) {
            return new HumanRace();
        } else {
            return new AlienRace();
        }
    }
}
