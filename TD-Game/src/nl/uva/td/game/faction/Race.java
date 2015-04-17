package nl.uva.td.game.faction;

import nl.uva.td.game.faction.tower.Tower;

public interface Race {

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

}
