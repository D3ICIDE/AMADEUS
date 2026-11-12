import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.concurrent.atomic.AtomicInteger;

public class Ui extends Application {

    // --- STATE VARIABLES ---
    public static final AtomicInteger point = new AtomicInteger(500);
    private static Ui instance;
    private int currentLevel = 0;
    private static final int COST_ARISE = 50;     // Cheap: Basic Hint
    private static final int COST_ASCEND = 100;    // Medium: Detailed Guide
    private static final int COST_ABSOLUTE = 250; // Ultimate: Costly

    AvatarConfig avatarConfig = new AvatarConfig();

    // --- GUI COMPONENTS ---
    private Stage primaryStage;
    private Popup popup;
    static ImageView view;
    public static Label scoreDisplay;
    public static Label levelNotification;
    private Label promptLabel;

    // --- CHAT & ANIMATION COMPONENTS ---
    private VBox responseContainer;
    private ResponseFormatter responseFormatter;
    private ThinkingIndicator currentThinkingIndicator;

    // --- ANIMATION ASSETS ---
    private RewardManager rewardEngine;
    private Timeline currentAnimation;

    final double POPUP_WIDTH = 550;
    final double POPUP_HEIGHT = 550;

    public static void main(String[] args) {
        launch(args);
    }

    public static Ui getInstance() {
        return instance;
    }

    @Override
    public void start(Stage worldLine) throws Exception {
        instance = this;
        this.primaryStage = worldLine;

        // Load images defined inside Avatar_Config
        avatarConfig.loadImages();

        this.rewardEngine = new RewardManager(this, point);
        rewardEngine.startPointIncrementer();

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        Scene theatre = new Scene(root, Color.BLACK);

        avatarConfig.setupCharacterView(root);

        // --- CHAT UI SETUP ---
        responseContainer = new VBox();
        responseContainer.setStyle("-fx-background-color: #000000;");
        VBox.setVgrow(responseContainer, Priority.ALWAYS);

        responseFormatter = new ResponseFormatter();
        VBox.setVgrow(responseFormatter, Priority.ALWAYS);

        responseFormatter.setOnPromptDisplayed(this::showThinking);

        currentThinkingIndicator = new ThinkingIndicator();
        currentThinkingIndicator.setVisible(false);
        currentThinkingIndicator.managedProperty().bind(currentThinkingIndicator.visibleProperty());

        responseContainer.getChildren().addAll(responseFormatter, currentThinkingIndicator);

        setupPopup(worldLine);

        updateAnimationTimeline(point.get());

        VoiceRecognition listener = new VoiceRecognition(this.instance);
        Thread listenerThread = new Thread(listener, "AMADEUS-Voice-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();

        // Set Window Icon via Avatar_Config asset
        if (avatarConfig.Icon != null) {
            worldLine.getIcons().add(avatarConfig.Icon);
        }

        worldLine.setTitle("AMADEUS");
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        worldLine.setX(screenBounds.getMaxX() - 200);
        worldLine.setY(screenBounds.getMaxY() - 250);
        worldLine.setWidth(200);
        worldLine.setHeight(250);
        worldLine.setScene(theatre);
        worldLine.setAlwaysOnTop(true);
        worldLine.setResizable(false);
        worldLine.show();
        worldLine.setOnCloseRequest(event -> Platform.exit());

        AnimationTimer scoreSync = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (scoreDisplay != null) scoreDisplay.setText("EXP: " + point.get());
            }
        };
        scoreSync.start();

        promptLabel = new Label("");
    }

    private String getKeywordFromInput(String input) {
        if (input == null) return "Command Arise";
        String lower = input.toLowerCase();
        if (lower.contains("absolute")) return "Command Absolute";
        if (lower.contains("ascend")) return "Command Ascend";
        return "Command Arise"; // Default fallback
    }

    private int getCostForKeyword(String keyword) {
        switch (keyword) {
            case "Command Absolute":
                return COST_ABSOLUTE;
            case "Command Ascend":
                return COST_ASCEND;
            default:
                return COST_ARISE;
        }
    }

    public void triggerAnalysis(String commandInput) {
        Platform.runLater(() -> {
            // 1. Identify Keyword
            String detectedKeyword = getKeywordFromInput(commandInput);

            // 2. Determine Cost based on Keyword
            int requiredCost = getCostForKeyword(detectedKeyword);

            // 3. Pre-Check: EXP Cost vs Current Points
            if (point.get() < requiredCost) {
                responseFormatter.clear();
                responseFormatter.appendText("System Status: Low EXP. '" + detectedKeyword + "' requires " + requiredCost + " EXP. Current: " + point.get());
                this.popup.show(primaryStage);
                return;
            }

            if (responseContainer == null) {
                System.err.println("Response container not initialized.");
                return;
            }

            // 4. Deduct the Specific Amount
            int currentPoints = point.addAndGet(-requiredCost);
            safeUpdateUI(currentPoints);

            // 5. Update UI
            responseFormatter.clear();
            responseFormatter.appendText(">> " + detectedKeyword + " initiated. Deducted " + requiredCost + " EXP.\n\n");
            this.popup.show(primaryStage);
            showThinking();

            // 6. Launch AI Thread with detected keyword
            Ai logic = new Ai(this, promptLabel, detectedKeyword);
            Thread logicThread = new Thread(logic, "AMADEUS-AI-Processor");
            logicThread.setDaemon(true);
            logicThread.start();
        });
    }

    // Overload for cases where no argument is passed (defaults to Arise)
    public void triggerAnalysis() {
        triggerAnalysis("Command Arise");
    }

    public ResponseFormatter getResponseFormatter() {
        return this.responseFormatter;
    }

    public void showThinking() {
        Platform.runLater(() -> {
            if (currentThinkingIndicator != null) {
                currentThinkingIndicator.setVisible(true);
                currentThinkingIndicator.start();
            }
        });
    }

    public void hideThinking() {
        Platform.runLater(() -> {
            if (currentThinkingIndicator != null) {
                currentThinkingIndicator.stop();
                currentThinkingIndicator.setVisible(false);
            }
        });
    }

    private void setupPopup(Stage worldLine) {
        this.popup = new Popup();
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 8 3 8;");
        closeButton.setOnAction(e -> this.popup.hide());

        BorderPane header = new BorderPane();
        Label title = new Label("AMADEUS AI");
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        header.setLeft(title);
        header.setRight(closeButton);

        VBox popupContent = new VBox(5);
        popupContent.getChildren().addAll(header, responseContainer);
        popupContent.setStyle("-fx-background-color: #000000; -fx-border-width: 2; -fx-padding: 10;");
        popupContent.setPrefSize(POPUP_WIDTH, POPUP_HEIGHT);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        this.popup.setX(screenBounds.getMaxX() - POPUP_WIDTH);
        this.popup.setY(screenBounds.getMinY());
        this.popup.getContent().add(popupContent);
    }

    public void safeUpdateUI(int currentPoint) {
        Platform.runLater(() -> {
            updateAnimationTimeline(currentPoint);
            if (scoreDisplay != null) scoreDisplay.setText("EXP: " + currentPoint);
        });
    }

    private void showLevelNotification(int newLevel, int oldLevel) {
        if (levelNotification == null) return;
        Platform.runLater(() -> {
            if (newLevel > oldLevel) {
                levelNotification.setText("LEVEL UP!");
                levelNotification.setStyle("-fx-text-fill: #00FF00; -fx-effect: dropshadow(one-pass-box, black, 3, 0.5, 0, 0);");
            } else {
                levelNotification.setText("LEVEL DOWN");
                levelNotification.setStyle("-fx-text-fill: #FF0000; -fx-effect: dropshadow(one-pass-box, black, 3, 0.5, 0, 0);");
            }
            levelNotification.setOpacity(1.0);
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1.5), levelNotification);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setDelay(Duration.seconds(2));
            fadeOut.play();
        });
    }

    private void updateAnimationTimeline(int currentPoint) {
        final double frame_len = 900.0;
        final double offset = 25;
        int newLevel = 0;
        if (currentPoint > 1800) newLevel = 4;
        else if (currentPoint > 1200) newLevel = 3;
        else if (currentPoint >= 800) newLevel = 2;
        else if (currentPoint >= 500) newLevel = 1;

        if (newLevel != this.currentLevel) {
            if (this.currentLevel != 0) showLevelNotification(newLevel, this.currentLevel);
        } else {
            return;
        }

        if (currentAnimation != null) currentAnimation.stop();

        this.currentLevel = newLevel;
        if (newLevel == 4) {
            Timeline LEVEL_4 = new Timeline(
                    getKeyFrame(avatarConfig.frame_11, offset, 0.0),
                    getKeyFrame(avatarConfig.frame_12, offset, frame_len),
                    getKeyFrame(avatarConfig.frame_11, offset, frame_len * 2)
            );
            LEVEL_4.setCycleCount(Animation.INDEFINITE);
            currentAnimation = LEVEL_4;
            currentAnimation.play();
        } else if (newLevel == 3) {
            Timeline LEVEL_3 = new Timeline(
                    getKeyFrame(avatarConfig.frame_05, offset, 0.0),
                    getKeyFrame(avatarConfig.frame_06, offset, frame_len),
                    getKeyFrame(avatarConfig.frame_07, offset, frame_len * 2),
                    getKeyFrame(avatarConfig.frame_08, offset, frame_len * 3),
                    getKeyFrame(avatarConfig.frame_09, offset, frame_len * 4)
            );
            LEVEL_3.setCycleCount(1);
            LEVEL_3.setOnFinished(event -> {
                Timeline LEVEL_3_1 = new Timeline(
                        getKeyFrame(avatarConfig.frame_09, offset, 0.0),
                        getKeyFrame(avatarConfig.frame_10, offset, frame_len),
                        getKeyFrame(avatarConfig.frame_09, offset, frame_len * 2)
                );
                LEVEL_3_1.setCycleCount(Animation.INDEFINITE);
                currentAnimation = LEVEL_3_1;
                currentAnimation.play();
            });
            currentAnimation = LEVEL_3;
            currentAnimation.play();
        } else if (newLevel == 2) {
            Timeline LEVEL_2 = new Timeline(
                    getKeyFrame(avatarConfig.frame_03, offset, 0.0),
                    getKeyFrame(avatarConfig.frame_04, offset, frame_len),
                    getKeyFrame(avatarConfig.frame_03, offset, frame_len * 2)
            );

            LEVEL_2.setCycleCount(Animation.INDEFINITE);
            currentAnimation = LEVEL_2;
            currentAnimation.play();
        } else if (newLevel == 1) {
            Timeline LEVEL_1 = new Timeline(
                    getKeyFrame(avatarConfig.frame_01, offset, 0.0),
                    getKeyFrame(avatarConfig.frame_02, offset + 3, frame_len),
                    getKeyFrame(avatarConfig.frame_01, offset, frame_len * 2)
            );
            LEVEL_1.setCycleCount(Animation.INDEFINITE);
            currentAnimation = LEVEL_1;
            currentAnimation.play();
        } else {
            Timeline LEVEL_0 = new Timeline(
                    getKeyFrame(avatarConfig.frame_13, offset, 0.0),
                    getKeyFrame(avatarConfig.frame_14, offset, frame_len),
                    getKeyFrame(avatarConfig.frame_13, offset, frame_len * 2)
            );
            LEVEL_0.setCycleCount(15);
            LEVEL_0.setOnFinished(event -> {
                Timeline LEVEL_0_1 = new Timeline(
                        getKeyFrame(avatarConfig.frame_15, offset, 0.0),
                        getKeyFrame(avatarConfig.frame_16, offset, frame_len),
                        getKeyFrame(avatarConfig.frame_15, offset, frame_len * 2)
                );
                LEVEL_0_1.setCycleCount(Animation.INDEFINITE);
                currentAnimation = LEVEL_0_1;
                currentAnimation.play();
            });
            currentAnimation = LEVEL_0;
            currentAnimation.play();
        }
    }

    public KeyFrame getKeyFrame(Image image, double offset, double duration) {
        if (duration == 0.0) {
            return new KeyFrame(Duration.ZERO, actionEvent -> {
                view.setImage(image);
                view.setTranslateX(offset);
            });
        } else {
            return new KeyFrame(Duration.millis(duration), actionEvent -> {
                view.setImage(image);
                view.setTranslateX(offset);
            });
        }
    }

    @Override
    public void stop() throws Exception {
        if (rewardEngine != null) rewardEngine.shutdownScheduler();
        super.stop();
    }
}