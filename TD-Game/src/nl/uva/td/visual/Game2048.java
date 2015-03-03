package nl.uva.td.visual;

import java.util.List;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import nl.uva.td.experiment.Score;
import nl.uva.td.game.CreepAgent;
import nl.uva.td.game.GameState;
import nl.uva.td.game.GameUpdateSubscriber;
import nl.uva.td.game.TowerAgent;
import nl.uva.td.game.map.GameField;
import nl.uva.td.game.map.Parser;
import nl.uva.td.game.tower.Tower;
import nl.uva.td.test.ListTowerPlacement;
import nl.uva.td.test.SpawnCreeps;

/**
 * @author bruno.borges@oracle.com
 */
public class Game2048 extends Application implements GameUpdateSubscriber {

    private nl.uva.td.visual.GameUIManager gameManager;
    private Bounds gameBounds;
    private final static int MARGIN = 36;

    private GameState mGameManager;

    private static String FIELD_NAME = "Standard3";
    private static int[] AI_LIST = { 13, 1, 12, 1, 11, 1, 18, 1, 15, 1, 20, 1 };

    @Override
    public void init() {
        // Downloaded from https://01.org/clear-sans/blogs
        // The font may be used and redistributed under the terms of the Apache License, Version
        // 2.0.

        GameField gameField = Parser.parseFile(FIELD_NAME);

        CreepAgent creepAgent = new SpawnCreeps();

        // int[] AIList = new int[] { 4, 1, 2, 1, 1, 1, 7, 1, 8, 1, 6, 1 };
        // int[] AIList = new int[] { 7, 1, 8, 1, 6, 1, 14, 1, 1, 1, 2, 1 };

        List<Integer> towerPlacements = ListTowerPlacement.generateAdvancedPlacesList(AI_LIST);
        List<Tower> towerTypes = ListTowerPlacement.generateAdvancedTowerList(AI_LIST);

        TowerAgent towerAgent = new ListTowerPlacement(towerTypes, towerPlacements);

        mGameManager = new GameState(creepAgent, towerAgent, gameField, true);
        mGameManager.subscriber(this);
        Font.loadFont(Game2048.class.getResource("ClearSans-Bold.ttf").toExternalForm(), 10.0);
    }

    @Override
    public void start(final Stage primaryStage) {
        gameManager = new nl.uva.td.visual.GameUIManager(mGameManager.getGameField());
        gameBounds = gameManager.getLayoutBounds();

        StackPane root = new StackPane(gameManager);
        root.getStyleClass().addAll("game-root");
        ChangeListener<Number> resize = (ov, v, v1) -> {
            double scale = Math.min((root.getWidth() - MARGIN) / gameBounds.getWidth(), (root.getHeight() - MARGIN)
                    / gameBounds.getHeight());
            gameManager.setScale(scale);
            gameManager.setLayoutX((root.getWidth() - gameBounds.getWidth()) / 2d);
            gameManager.setLayoutY((root.getHeight() - gameBounds.getHeight()) / 2d);
        };
        root.widthProperty().addListener(resize);
        root.heightProperty().addListener(resize);

        Scene scene = new Scene(root);
        scene.getStylesheets().add("nl/uva/td/visual/game.css");
        addKeyHandler(scene);
        addSwipeHandlers(scene);

        if (isARMDevice()) {
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
        }

        if (Platform.isSupported(ConditionalFeature.INPUT_TOUCH)) {
            scene.setCursor(Cursor.NONE);
        }

        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double factor = Math.min(visualBounds.getWidth() / (gameBounds.getWidth() + MARGIN), visualBounds.getHeight()
                / (gameBounds.getHeight() + MARGIN));
        primaryStage.setTitle("Tower Defence AI");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(gameBounds.getWidth() / 2d);
        primaryStage.setMinHeight(gameBounds.getHeight() / 2d);
        primaryStage.setWidth((gameBounds.getWidth() + MARGIN) * factor);
        primaryStage.setHeight((gameBounds.getHeight() + MARGIN) * factor);
        primaryStage.show();

        mGameManager.start();
    }

    private boolean isARMDevice() {
        return System.getProperty("os.arch").toUpperCase().contains("ARM");
    }

    static boolean resume = false;

    @Override
    public void update(final Score score) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                gameManager.updateCreeps(mGameManager.getGameField(), score);
            }
        });

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addKeyHandler(final Scene scene) {
        scene.setOnKeyPressed(ke -> {
            KeyCode keyCode = ke.getCode();
            if (keyCode.equals(KeyCode.S)) {
                gameManager.saveSession();
                return;
            }
            if (keyCode.equals(KeyCode.R)) {
                resume = true;
                // gameManager.restoreSession();
                return;
            }
            if (keyCode.equals(KeyCode.P)) {
                gameManager.pauseGame();
                return;
            }
            if (keyCode.equals(KeyCode.Q) || keyCode.equals(KeyCode.ESCAPE)) {
                gameManager.quitGame();
                return;
            }
            if (keyCode.isArrowKey()) {
                Direction direction = Direction.valueFor(keyCode);
                gameManager.move(direction);
            }
        });
    }

    private void addSwipeHandlers(final Scene scene) {
        scene.setOnSwipeUp(e -> gameManager.move(Direction.UP));
        scene.setOnSwipeRight(e -> gameManager.move(Direction.RIGHT));
        scene.setOnSwipeLeft(e -> gameManager.move(Direction.LEFT));
        scene.setOnSwipeDown(e -> gameManager.move(Direction.DOWN));
    }

    public static void show(final int[] positions, final String fieldName) {
        AI_LIST = positions;
        FIELD_NAME = fieldName;
        launch();
    }

    public static void main(final String[] args) {
        launch();
    }

    @Override
    public void stop() {
        gameManager.saveRecord();
        mGameManager.stop();
    }
}
