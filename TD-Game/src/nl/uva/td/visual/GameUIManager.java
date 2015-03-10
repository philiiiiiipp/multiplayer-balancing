package nl.uva.td.visual;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.scene.Group;
import javafx.util.Duration;
import nl.uva.td.experiment.Score;
import nl.uva.td.game.faction.alien.tower.ChainLightningTower;
import nl.uva.td.game.faction.alien.tower.ParasiteTower;
import nl.uva.td.game.faction.alien.tower.ShockTower;
import nl.uva.td.game.faction.human.tower.ArcherTower;
import nl.uva.td.game.faction.human.tower.FireTower;
import nl.uva.td.game.faction.human.tower.IceTower;
import nl.uva.td.game.map.CreepField;
import nl.uva.td.game.map.Field;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.TowerField;

/**
 *
 * @author bruno
 */
public class GameUIManager extends Group {

    public static final int FINAL_VALUE_TO_WIN = 2048;

    private static final Duration ANIMATION_EXISTING_TILE = Duration.millis(65);
    private static final Duration ANIMATION_NEWLY_ADDED_TILE = Duration.millis(125);
    private static final Duration ANIMATION_MERGED_TILE = Duration.millis(80);

    private volatile boolean movingTiles = false;
    private final List<Location> locations = new ArrayList<>();
    private final Map<Location, Tile> gameGrid;
    private final Set<Tile> mergedToBeRemoved = new HashSet<>();
    private final ParallelTransition parallelTransition = new ParallelTransition();

    private final Board board;
    private final GridOperator gridOperator;

    public GameUIManager(final GameField gameField) {
        this(gameField.getGameField().length, gameField);
    }

    /**
     * GameManager is a Group containing a Board that holds a grid and the score a Map holds the
     * location of the tiles in the grid
     *
     * The purpose of the game is sum the value of the tiles up to 2048 points Based on the
     * Javascript version: https://github.com/gabrielecirulli/2048
     *
     * @param gridSize
     *            defines the size of the grid, default 4x4
     */
    public GameUIManager(final int gridSize, final GameField gameField) {
        this.gameGrid = new HashMap<>();

        gridOperator = new GridOperator(gameField.getGameField().length, gameField.getGameField()[0].length);
        board = new Board(gridOperator, gameField);
        this.getChildren().add(board);

        board.clearGameProperty().addListener((ov, b, b1) -> {
            if (b1) {
                initializeGameGrid();
            }
        });
        board.resetGameProperty().addListener((ov, b, b1) -> {
            if (b1) {
                startGame();
            }
        });

        initializeGameGrid();
        startGame();
    }

    /**
     * Initializes all cells in gameGrid map to null
     */
    private void initializeGameGrid() {
        gameGrid.clear();
        locations.clear();
        gridOperator.traverseGrid((x, y) -> {
            Location thisloc = new Location(x, y);
            locations.add(thisloc);
            gameGrid.put(thisloc, null);
            return 0;
        });
    }

    /**
     * Starts the game by adding 1 or 2 tiles at random locations
     */
    private void startGame() {
        // Tile tile0 = Tile.newRandomTile();
        // List<Location> randomLocs = new ArrayList<>(locations);
        // Collections.shuffle(randomLocs);
        // Iterator<Location> locs = randomLocs.stream().limit(2).iterator();
        // tile0.setLocation(locs.next());
        //
        // Tile tile1 = null;
        // if (new Random().nextFloat() <= 0.8) { // gives 80% chance to add a second tile
        // tile1 = Tile.newRandomTile();
        // if (tile1.getValue() == 4 && tile0.getValue() == 4) {
        // tile1 = Tile.newTile(2);
        // }
        // tile1.setLocation(locs.next());
        // }
        //
        // Arrays.asList(tile0, tile1).stream().filter(Objects::nonNull).forEach(t ->
        // gameGrid.put(t.getLocation(), t));

        redrawTilesInGameGrid();

        board.startGame();
    }

    /**
     * Redraws all tiles in the <code>gameGrid</code> object
     */
    private void redrawTilesInGameGrid() {
        gameGrid.values().stream().filter(Objects::nonNull).forEach(t -> board.addTile(t));
    }

    public void updateCreeps(final GameField gameField, final Score score) {

        if (board.getPoints() != score.getGold()) {
            board.setPoints(score.getGold());
        }

        if (board.getLives() != score.getLivesLeft()) {
            board.removeLives(board.getLives() - score.getLivesLeft());
        }

        gridOperator.traverseGrid((x, y) -> {

            Location thisloc = new Location(y, x);
            Optional<Tile> opTile = optionalTile(thisloc);

            Field field = gameField.getGameField()[x][y];
            if (field instanceof CreepField) {
                CreepField f = (CreepField) field;

                if (opTile.isPresent()) {

                    if (!f.hasCreeps()) {
                        gameGrid.replace(thisloc, null);
                        board.getGridGroup().getChildren().remove(opTile.get());
                    } else {
                        opTile.get().update(f.getCreep().getHealth(), f.getCreep().getMaxHealth());
                    }
                } else if (f.hasCreeps()) {

                    Tile tile = Tile.newTile(f.getCreep().getHealth(), f.getCreep().getMaxHealth());
                    tile.setLocation(thisloc);
                    board.addTile(tile);
                    gameGrid.put(tile.getLocation(), tile);

                    animateNewlyAddedTile(tile).play();
                }
            } else {
                TowerField towerField = (TowerField) field;
                if (towerField.getTower() != null) {
                    if (!opTile.isPresent()) {
                        Tile tile = null;

                        if (towerField.getTower() instanceof FireTower) {
                            tile = Tile.newTile(Tile.FIRE_TOWER_NUM, 1);
                        } else if (towerField.getTower() instanceof IceTower) {
                            tile = Tile.newTile(Tile.ICE_TOWER_NUM, 1);
                        } else if (towerField.getTower() instanceof ArcherTower) {
                            tile = Tile.newTile(Tile.ARCHER_TOWER_NUM, 1);
                        } else if (towerField.getTower() instanceof ChainLightningTower) {
                            tile = Tile.newTile(Tile.CHAIN_LIGHTNING_TOWER_NUM, 1);
                        } else if (towerField.getTower() instanceof ShockTower) {
                            tile = Tile.newTile(Tile.SHOCK_TOWER_NUM, 1);
                        } else if (towerField.getTower() instanceof ParasiteTower) {
                            tile = Tile.newTile(Tile.PARASITE_TOWER_NUM, 1);
                        } else {
                            throw new RuntimeException("Nono");
                        }

                        tile.setLocation(thisloc);
                        board.addTile(tile);
                        gameGrid.put(tile.getLocation(), tile);

                        animateNewlyAddedTile(tile).play();
                    }

                } else {
                    if (opTile.isPresent()) {
                        gameGrid.replace(thisloc, null);
                        board.getGridGroup().getChildren().remove(opTile.get());
                    }
                }
            }

            return 1;
        });

        // Animates score and lives
        board.animateScore();

        if (score.getLivesLeft() <= 0) {
            board.setGameWin(true);
        }
    }

    /**
     * Moves the tiles according to given direction At any move, takes care of merge tiles, add a
     * new one and perform the required animations It updates the score and checks if the user won
     * the game or if the game is over
     *
     * @param direction
     *            is the selected direction to move the tiles
     */
    private void moveTiles(final Direction direction) {
        synchronized (gameGrid) {
            if (movingTiles) {
                return;
            }
        }

        board.setPoints(0);

        gridOperator.sortGrid(direction);
        final int tilesWereMoved = gridOperator.traverseGrid((x, y) -> {
            Location thisloc = new Location(x, y);
            Location farthestLocation = findFarthestLocation(thisloc, direction); // farthest
            // available
            // location
            Optional<Tile> opTile = optionalTile(thisloc);

            AtomicInteger result = new AtomicInteger();
            Location nextLocation = farthestLocation.offset(direction); // calculates to a
            // possible merge
            optionalTile(nextLocation).filter(t -> t.isMergeable(opTile) && !t.isMerged()).ifPresent(t -> {
                Tile tile = opTile.get();
                t.merge(tile);
                t.toFront();
                gameGrid.put(nextLocation, t);
                gameGrid.replace(thisloc, null);

                parallelTransition.getChildren().add(animateExistingTile(tile, t.getLocation()));
                parallelTransition.getChildren().add(animateMergedTile(t));
                mergedToBeRemoved.add(tile);

                board.addPoints(t.getValue());

                if (t.getValue() == FINAL_VALUE_TO_WIN) {
                    board.setGameWin(true);
                }
                result.set(1);
            });
            if (result.get() == 0 && opTile.isPresent() && !farthestLocation.equals(thisloc)) {
                Tile tile = opTile.get();
                parallelTransition.getChildren().add(animateExistingTile(tile, farthestLocation));

                gameGrid.put(farthestLocation, tile);
                gameGrid.replace(thisloc, null);

                tile.setLocation(farthestLocation);

                result.set(1);
            }

            return result.get();
        });

        board.animateScore();

        parallelTransition.setOnFinished(e -> {
            synchronized (gameGrid) {
                movingTiles = false;
            }

            board.getGridGroup().getChildren().removeAll(mergedToBeRemoved);

            Location randomAvailableLocation = findRandomAvailableLocation();
            if (randomAvailableLocation == null && mergeMovementsAvailable() == 0) {
                // game is over if there are no more moves available
                board.setGameOver(true);
            } else if (randomAvailableLocation != null && tilesWereMoved > 0) {
                addAndAnimateRandomTile(randomAvailableLocation);
            }

            mergedToBeRemoved.clear();

            // reset merged after each movement
            gameGrid.values().stream().filter(Objects::nonNull).forEach(Tile::clearMerge);
        });

        synchronized (gameGrid) {
            movingTiles = true;
        }

        parallelTransition.play();
        parallelTransition.getChildren().clear();
    }

    /**
     * optionalTile allows using tiles from the map at some location, whether they are null or not
     *
     * @param loc
     *            location of the tile
     * @return an Optional<Tile> containing null or a valid tile
     */
    private Optional<Tile> optionalTile(final Location loc) {
        return Optional.ofNullable(gameGrid.get(loc));
    }

    /**
     * Searchs for the farthest empty location where the current tile could go
     *
     * @param location
     *            of the tile
     * @param direction
     *            of movement
     * @return a location
     */
    private Location findFarthestLocation(Location location, final Direction direction) {
        Location farthest;

        do {
            farthest = location;
            location = farthest.offset(direction);
        } while (gridOperator.isValidLocation(location) && !optionalTile(location).isPresent());

        return farthest;
    }

    /**
     * Finds the number of pairs of tiles that can be merged
     *
     * This method is called only when the grid is full of tiles, what makes the use of Optional
     * unnecessary, but it could be used when the board is not full to find the number of pairs of
     * mergeable tiles and provide a hint for the user, for instance
     *
     * @return the number of pairs of tiles that can be merged
     */
    private int mergeMovementsAvailable() {
        final AtomicInteger pairsOfMergeableTiles = new AtomicInteger();

        Stream.of(Direction.UP, Direction.LEFT).parallel().forEach(direction -> {
            gridOperator.traverseGrid((x, y) -> {
                Location thisloc = new Location(x, y);
                optionalTile(thisloc).ifPresent(t -> {
                    if (t.isMergeable(optionalTile(thisloc.offset(direction)))) {
                        pairsOfMergeableTiles.incrementAndGet();
                    }
                });
                return 0;
            });
        });
        return pairsOfMergeableTiles.get();
    }

    /**
     * Finds a random location or returns null if none exist
     *
     * @return a random location or <code>null</code> if there are no more locations available
     */
    private Location findRandomAvailableLocation() {
        List<Location> availableLocations = locations.stream().filter(l -> gameGrid.get(l) == null)
                .collect(Collectors.toList());

        if (availableLocations.isEmpty()) {
            return null;
        }

        Collections.shuffle(availableLocations);
        Location randomLocation = availableLocations.get(new Random().nextInt(availableLocations.size()));
        return randomLocation;
    }

    /**
     * Adds a tile of random value to a random location with a proper animation
     *
     * @param randomLocation
     */
    private void addAndAnimateRandomTile(final Location randomLocation) {
        Tile tile = board.addRandomTile(randomLocation);
        gameGrid.put(tile.getLocation(), tile);

        animateNewlyAddedTile(tile).play();
    }

    /**
     * Animation that creates a fade in effect when a tile is added to the game by increasing the
     * tile scale from 0 to 100%
     *
     * @param tile
     *            to be animated
     * @return a scale transition
     */
    private ScaleTransition animateNewlyAddedTile(final Tile tile) {
        final ScaleTransition scaleTransition = new ScaleTransition(ANIMATION_NEWLY_ADDED_TILE, tile);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.setInterpolator(Interpolator.EASE_OUT);
        scaleTransition.setOnFinished(e -> {
            // after last movement on full grid, check if there are movements available
            if (this.gameGrid.values().parallelStream().noneMatch(Objects::isNull)
                    && mergeMovementsAvailable() == 0) {
                board.setGameOver(true);
            }
        });
        return scaleTransition;
    }

    /**
     * Animation that moves the tile from its previous location to a new location
     *
     * @param tile
     *            to be animated
     * @param newLocation
     *            new location of the tile
     * @return a timeline
     */
    private Timeline animateExistingTile(final Tile tile, final Location newLocation) {
        Timeline timeline = new Timeline();
        KeyValue kvX = new KeyValue(tile.layoutXProperty(), newLocation.getLayoutX(Board.CELL_SIZE)
                - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);
        KeyValue kvY = new KeyValue(tile.layoutYProperty(), newLocation.getLayoutY(Board.CELL_SIZE)
                - (tile.getMinHeight() / 2), Interpolator.EASE_OUT);

        KeyFrame kfX = new KeyFrame(ANIMATION_EXISTING_TILE, kvX);
        KeyFrame kfY = new KeyFrame(ANIMATION_EXISTING_TILE, kvY);

        timeline.getKeyFrames().add(kfX);
        timeline.getKeyFrames().add(kfY);

        return timeline;
    }

    /**
     * Animation that creates a pop effect when two tiles merge by increasing the tile scale to 120%
     * at the middle, and then going back to 100%
     *
     * @param tile
     *            to be animated
     * @return a sequential transition
     */
    private SequentialTransition animateMergedTile(final Tile tile) {
        final ScaleTransition scale0 = new ScaleTransition(ANIMATION_MERGED_TILE, tile);
        scale0.setToX(1.2);
        scale0.setToY(1.2);
        scale0.setInterpolator(Interpolator.EASE_IN);

        final ScaleTransition scale1 = new ScaleTransition(ANIMATION_MERGED_TILE, tile);
        scale1.setToX(1.0);
        scale1.setToY(1.0);
        scale1.setInterpolator(Interpolator.EASE_OUT);

        return new SequentialTransition(scale0, scale1);
    }

    /*************************************************************************/
    /************************ Public methods *********************************/
    /*************************************************************************/

    /**
     * Move the tiles according user input if overlay is not on
     *
     * @param direction
     */
    public void move(final Direction direction) {
        if (!board.isLayerOn().get()) {
            moveTiles(direction);
        }
    }

    /**
     * Set gameManager scale to adjust overall game size
     *
     * @param scale
     */
    public void setScale(final double scale) {
        this.setScaleX(scale);
        this.setScaleY(scale);
    }

    /**
     * Check if overlay covers the grid or not
     *
     * @return
     */
    public BooleanProperty isLayerOn() {
        return board.isLayerOn();
    }

    /**
     * Pauses the game time, covers the grid
     */
    public void pauseGame() {
        board.pauseGame();
    }

    /**
     * Quit the game with confirmation
     */
    public void quitGame() {
        board.quitGame();
    }

    /**
     * Save the game to a properties file
     */
    public void saveSession() {
        board.saveSession(gameGrid);
    }

    /**
     * Restore the game from a properties file, without confirmation
     */
    public void restoreSession() {
        if (board.restoreSession(gameGrid)) {
            redrawTilesInGameGrid();
        }
    }

    /**
     * Save actual record to a properties file
     */
    public void saveRecord() {
        board.saveRecord();
    }

}
