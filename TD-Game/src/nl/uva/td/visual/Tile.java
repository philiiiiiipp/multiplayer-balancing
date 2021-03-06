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
    private final static String FIRE_TOWER = "fire_tower-tile-placed";

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
        } else if (fitness == -1) {
            getStyleClass().addAll("game-label", ICE_TOWER);
            setText("T");
        } else if (fitness == -2) {
            getStyleClass().addAll("game-label", FIRE_TOWER);
            setText("T");
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
