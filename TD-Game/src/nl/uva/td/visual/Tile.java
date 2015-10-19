package nl.uva.td.visual;

import java.text.DecimalFormat;
import java.util.Optional;
import java.util.Random;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

/**
 * @author bruno.borges@oracle.com
 */
public class Tile extends Label {

    private final static String TOP_HEALTH = "creep-tile-top-health";
    private final static String MIDDLE_HEALTH = "creep-tile-middle-health";
    private final static String LOW_HEALTH = "creep-tile-low-health";

    private final static String ICE_TOWER = "ice_tower-tile-placed";
    private final static String FIRE_TOWER = "ice_tower-tile-placed"; // "fire_tower-tile-placed";
    private final static String ARCHER_TOWER = "ice_tower-tile-placed"; // "archer_tower-tile-placed";
    private final static String CHAIN_LIGHTNING_TOWER = "ice_tower-tile-placed"; // "chain_lightning_tower-tile-placed";
    private final static String SHOCK_TOWER = "ice_tower-tile-placed"; // "shock_tower-tile-placed";
    private final static String PARASITE_TOWER = "ice_tower-tile-placed"; // "parasite_tower-tile-placed";

    public final static double ICE_TOWER_NUM = -1;
    public final static double FIRE_TOWER_NUM = -2;
    public final static double ARCHER_TOWER_NUM = -3;
    public final static double CHAIN_LIGHTNING_TOWER_NUM = -4;
    public final static double SHOCK_TOWER_NUM = -5;
    public final static double PARASITE_TOWER_NUM = -6;

    private final double mMaxValue;
    private double mHealth;
    private Location location;
    private Boolean merged;

    public static Tile newRandomTile() {
        int value = new Random().nextDouble() < 0.9 ? 2 : 4;
        return new Tile(value, 100);
    }

    public static Tile newTile(final double health, final double maxValue) {
        return new Tile(health, maxValue);
    }

    private Tile(final double health, final double maxValue) {
        mMaxValue = maxValue;

        final int squareSize = Board.CELL_SIZE - 13;
        setMinSize(squareSize, squareSize);
        setMaxSize(squareSize, squareSize);
        setPrefSize(squareSize, squareSize);
        setAlignment(Pos.CENTER);

        update(health, maxValue);
    }

    public void update(final double health, final double maxHealth) {
        this.mHealth = health;
        this.merged = false;
        DecimalFormat df = new DecimalFormat("0.0");
        setText(df.format(mHealth));

        double fitness = mHealth / mMaxValue;
        if (fitness > 0.9) {

            getStyleClass().addAll("game-label", TOP_HEALTH);
        } else if (fitness > 0.1) {

            getStyleClass().addAll("game-label", MIDDLE_HEALTH);
        } else if (fitness >= 0) {

            getStyleClass().addAll("game-label", LOW_HEALTH);
        } else if (fitness == ICE_TOWER_NUM) {
            getStyleClass().addAll("game-label", ICE_TOWER);
            setText("Ice");
        } else if (fitness == FIRE_TOWER_NUM) {
            getStyleClass().addAll("game-label", FIRE_TOWER);
            setText("Fire");
        } else if (fitness == ARCHER_TOWER_NUM) {
            getStyleClass().addAll("game-label", ARCHER_TOWER);
            setText("Arch");
        } else if (fitness == CHAIN_LIGHTNING_TOWER_NUM) {
            getStyleClass().addAll("game-label", CHAIN_LIGHTNING_TOWER);
            setText("Light");
        } else if (fitness == SHOCK_TOWER_NUM) {
            getStyleClass().addAll("game-label", SHOCK_TOWER);
            setText("Shock");
        } else if (fitness == PARASITE_TOWER_NUM) {
            getStyleClass().addAll("game-label", PARASITE_TOWER);
            setText("Para");
        } else {
            // This shows a mistake
            getStyleClass().addAll("game-label", TOP_HEALTH);
        }

    }

    public void merge(final Tile another) {
        getStyleClass().remove("game-tile-" + mHealth);
        this.mHealth += another.getValue();

        DecimalFormat df = new DecimalFormat("0.0");
        setText(df.format(mHealth));
        merged = true;
        getStyleClass().add("game-tile-" + mHealth);
    }

    public Integer getValue() {
        return (int) mHealth;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(final Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Tile{" + "value=" + mHealth + ", location=" + location + '}';
    }

    public boolean isMerged() {
        return merged;
    }

    public void clearMerge() {
        merged = false;
    }

    public boolean isMergeable(final Optional<Tile> anotherTile) {
        return anotherTile.filter(t -> t.getValue().equals(getValue())).isPresent();
    }
}
