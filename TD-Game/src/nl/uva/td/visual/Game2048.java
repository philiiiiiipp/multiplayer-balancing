package nl.uva.td.visual;

import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import nl.uva.td.experiment.Score;
import nl.uva.td.game.GameUpdateHUB;
import nl.uva.td.game.GameUpdateSubscriber;
import nl.uva.td.game.map.GameField;

/**
 * @author bruno.borges@oracle.com
 */
public class Game2048 extends Application implements GameUpdateSubscriber {

    private static List<GameUIManager> sGameManagers = new LinkedList<GameUIManager>();
    private static int sCurrentUIManagerCount = 0;

    private Bounds gameBounds;
    private final static int MARGIN = 36;

    public static Game2048 sInstance;

    @Override
    public void init() {
        // Downloaded from https://01.org/clear-sans/blogs
        // The font may be used and redistributed under the terms of the Apache License, Version
        // 2.0.
        Font.loadFont(Game2048.class.getResource("ClearSans-Bold.ttf").toExternalForm(), 10.0);
    }

    @Override
    public void start(final Stage primaryStage) {
        sInstance = this;
    }

    public void showMore(final GameUpdateHUB mGameState, final String aiName) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                Stage primaryStage = new Stage();

                GameUIManager gameManager = new nl.uva.td.visual.GameUIManager(mGameState.getGameField());
                sGameManagers.add(gameManager);
                Bounds gameBounds = gameManager.getLayoutBounds();

                StackPane root = new StackPane(gameManager);
                root.getStyleClass().addAll("game-root");
                ChangeListener<Number> resize = (ov, v, v1) -> {
                    double scale = Math.min((root.getWidth() - MARGIN) / gameBounds.getWidth(),
                            (root.getHeight() - MARGIN) / gameBounds.getHeight());
                    gameManager.setScale(scale);
                    gameManager.setLayoutX((root.getWidth() - gameBounds.getWidth()) / 2d);
                    gameManager.setLayoutY((root.getHeight() - gameBounds.getHeight()) / 2d);
                };
                root.widthProperty().addListener(resize);
                root.heightProperty().addListener(resize);

                Scene scene = new Scene(root);
                scene.getStylesheets().add("nl/uva/td/visual/game.css");

                if (sInstance.isARMDevice()) {
                    primaryStage.setFullScreen(true);
                    primaryStage.setFullScreenExitHint("");
                }

                if (Platform.isSupported(ConditionalFeature.INPUT_TOUCH)) {
                    scene.setCursor(Cursor.NONE);
                }

                Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
                double factor = Math.min(visualBounds.getWidth() / (gameBounds.getWidth() + MARGIN),
                        visualBounds.getHeight() / (gameBounds.getHeight() + MARGIN));
                primaryStage.setTitle(aiName);
                primaryStage.setScene(scene);
                primaryStage.setMinWidth(gameBounds.getWidth() / 2d);
                primaryStage.setMinHeight(gameBounds.getHeight() / 2d);
                primaryStage.setWidth((gameBounds.getWidth() + MARGIN) * factor);
                primaryStage.setHeight((gameBounds.getHeight() + MARGIN) * factor);
                primaryStage.show();

                mGameState.subscriber(sInstance, sCurrentUIManagerCount++);
            }
        });
    }

    private boolean isARMDevice() {
        return System.getProperty("os.arch").toUpperCase().contains("ARM");
    }

    static boolean resume = false;

    @Override
    public void update(final Score score, final GameField gameField, final int gameManagerID) {
        Platform.runLater(new Runnable() {

            @Override
            public void run() {
                sGameManagers.get(gameManagerID).updateCreeps(gameField, score);
            }
        });

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void show() {
        launch();
    }

    @Override
    public void stop() {}
}
