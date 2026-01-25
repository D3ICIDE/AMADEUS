package com.AMADEUS.phase;
import com.AMADEUS.phase.AMADEUS_PHASE_2;
import com.AMADEUS.phase.ResponseFormatter;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;


import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AMADEUS_PHASE_3 extends Application {

    // --- STATE VARIABLES ---
    public static final AtomicInteger point = new AtomicInteger(500);
    private static AMADEUS_PHASE_3 instance;
    private int currentLevel = 0;
    private static final int COST_ARISE = 50;     // Cheap: Basic Hint
    private static final int COST_ASCEND = 100;    // Medium: Detailed Guide
    private static final int COST_ABSOLUTE = 250; //Ultimate: Costly

    // For testing: Cycle through the commands
    private int testCommandIndex = 0;
    private final String[] TEST_COMMANDS = {
            "Command Arise",
            "Command Ascend",
            "Command Absolute"
    };

    // --- GUI COMPONENTS ---
    private Stage primaryStage;
    private Popup popup;
    private ImageView view;
    private Label scoreDisplay;
    private Label levelNotification;
    private Label promptLabel;

    // --- CHAT & ANIMATION COMPONENTS ---
    private VBox responseContainer;
    private ResponseFormatter responseFormatter;
    private ThinkingIndicator currentThinkingIndicator;

    // --- ANIMATION ASSETS ---
    private RewardManager rewardEngine;
    private Timeline currentAnimation;
    private Image frame_01, frame_02, frame_03, frame_04, frame_05, frame_06,
            frame_07, frame_08, frame_09, frame_10, frame_11, frame_12, frame_13, frame_14, frame_15, frame_16,Icon;

    final double POPUP_WIDTH = 550;
    final double POPUP_HEIGHT = 550;

    public static void main(String[] args) {
        launch(args);
    }

    public static AMADEUS_PHASE_3 getInstance() {
        return instance;
    }

    @Override
    public void start(Stage worldLine) throws Exception {
        instance = this;
        this.primaryStage = worldLine;

        loadImages();

        this.rewardEngine = new RewardManager(this, point);
        rewardEngine.startPointIncrementer();

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        Scene theatre = new Scene(root, Color.BLACK);

        setupCharacterView(root);

        // --- CHAT UI SETUP ---
        responseContainer = new VBox();
        responseContainer.setStyle("-fx-background-color: #0A1931;");
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

        AMADEUS_VOICE_RECOG listener = new AMADEUS_VOICE_RECOG(this.instance);
        Thread listenerThread = new Thread(listener, "AMADEUS-Voice-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();

        Icon = new Image("jjk.jpg");
        worldLine.getIcons().add(Icon);

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
                if(scoreDisplay != null) scoreDisplay.setText("EXP: " + point.get());
            }
        };
        scoreSync.start();

        promptLabel = new Label("");
    }

    /**
     * Determines which specific command/model to use based on the input string.
     */

private String getKeywordFromInput(String input) {
    if (input == null) return "Command Arise";
    String lower = input.toLowerCase();
    if (lower.contains("absolute")) return "Command Absolute";
    if (lower.contains("ascend")) return "Command Ascend";
    if (lower.contains("arise")) return "Command Arise";
    return "Command Arise"; // Default fallback
}
        private int getCostForKeyword(String keyword) {
            switch (keyword) {
                case "Command Absolute": return COST_ABSOLUTE;
                case "Command Ascend":   return COST_ASCEND;
                case "Command Arise":    return COST_ARISE;
                default:                 return COST_ARISE;
            }
        }

        /**
         * Main entry point for starting an analysis.
         * Called by both the Voice Recognition thread AND the Test Button.
         * * @param commandInput The command string (e.g., "Command Arise").
         */

        public void triggerAnalysis(String commandInput) {
            Platform.runLater(() -> {
                // 1. Identify the Keyword
                String detectedKeyword = getKeywordFromInput(commandInput);

                // 2. Determine Cost based on Keyword
                int requiredCost = getCostForKeyword(detectedKeyword);

                // 3. Pre-Check: EXP Cost vs Current Points
                if (point.get() < requiredCost) {
                    responseFormatter.clear();
                    // appendText is available via the inner class below
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
                AMADEUS_PHASE_2 logic = new AMADEUS_PHASE_2(this, promptLabel, detectedKeyword);
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

        private void setupCharacterView(StackPane root) {
            view = new ImageView(frame_01);
            view.setFitWidth(200);
            view.setFitHeight(250);
            view.setTranslateX(25);
            view.preserveRatioProperty().set(true);

            scoreDisplay = new Label("EXP: " + point.get());
            scoreDisplay.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
            scoreDisplay.setStyle("-fx-text-fill: #00FFFF; -fx-effect: dropshadow(one-pass-box, darkgreen, 4, 0.5, 0, 0);");
            scoreDisplay.setTranslateY(10);

            levelNotification = new Label("");
            levelNotification.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
            levelNotification.setOpacity(0);
            levelNotification.setTranslateY(30);

            // --- UPDATED TEST BUTTON ---
            // Defaults to the first command in the list
            Button testButton = new Button("TEST: " + TEST_COMMANDS[0].replace("Command ", ""));
            testButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00FFFF; -fx-border-color: #00FFFF; -fx-border-radius: 5; -fx-font-size: 10px; -fx-cursor: hand;");

            testButton.setOnAction(e -> {
                // 1. Get current command
                String commandToTest = TEST_COMMANDS[testCommandIndex];

                // 2. Trigger the analysis (simulating voice input)
                triggerAnalysis(commandToTest);

                // 3. Cycle index for next click
                testCommandIndex = (testCommandIndex + 1) % TEST_COMMANDS.length;

                // 4. Update button text to show what the NEXT click will do
                String nextCmdName = TEST_COMMANDS[testCommandIndex].replace("Command ", "");
                testButton.setText("TEST: " + nextCmdName);
            });

            // Hover effect for the button
            testButton.setOnMouseEntered(e -> testButton.setStyle("-fx-background-color: #00FFFF; -fx-text-fill: black; -fx-border-color: #00FFFF; -fx-border-radius: 5; -fx-font-size: 10px; -fx-cursor: hand;"));
            testButton.setOnMouseExited(e -> testButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00FFFF; -fx-border-color: #00FFFF; -fx-border-radius: 5; -fx-font-size: 10px; -fx-cursor: hand;"));

            root.getChildren().addAll(view, scoreDisplay, levelNotification, testButton);
            StackPane.setAlignment(scoreDisplay, Pos.TOP_LEFT);
            StackPane.setAlignment(levelNotification, Pos.TOP_LEFT);
            StackPane.setAlignment(testButton, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(testButton, new javafx.geometry.Insets(10));
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
            popupContent.setStyle("-fx-background-color: #0A1931; -fx-border-color: #b39595; -fx-border-width: 2; -fx-padding: 10;");
            popupContent.setPrefSize(POPUP_WIDTH, POPUP_HEIGHT);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            this.popup.setX(screenBounds.getMaxX() - POPUP_WIDTH);
            this.popup.setY(screenBounds.getMinY());
            this.popup.getContent().add(popupContent);
        }

        public void safeUpdateUI(int currentPoint) {
            Platform.runLater(() -> {
                updateAnimationTimeline(currentPoint);
                if(scoreDisplay != null) scoreDisplay.setText("EXP: " + currentPoint);
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
                        new KeyFrame(Duration.ZERO, e -> { view.setImage(frame_11); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len), e -> { view.setImage(frame_12); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len * 2), e -> { view.setImage(frame_11); view.setTranslateX(offset); })
                );
                LEVEL_4.setCycleCount(Animation.INDEFINITE);
                currentAnimation = LEVEL_4;
                currentAnimation.play();
            } else if (newLevel == 3) {
                Timeline LEVEL_3 = new Timeline(
                        new KeyFrame(Duration.ZERO, e -> { view.setImage(frame_05); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len), e -> { view.setImage(frame_06); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len * 2), e -> { view.setImage(frame_07); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len * 3), e -> { view.setImage(frame_08); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len * 4), e -> { view.setImage(frame_09); view.setTranslateX(offset); })
                );
                LEVEL_3.setCycleCount(1);
                LEVEL_3.setOnFinished(event -> {
                    Timeline LEVEL_3_1 = new Timeline(
                            new KeyFrame(Duration.ZERO, e -> { view.setImage(frame_09); }),
                            new KeyFrame(Duration.millis(frame_len), e -> { view.setImage(frame_10); view.setTranslateX(offset); }),
                            new KeyFrame(Duration.millis(frame_len * 2), e -> { view.setImage(frame_09); view.setTranslateX(offset); })
                    );
                    LEVEL_3_1.setCycleCount(Animation.INDEFINITE);
                    currentAnimation = LEVEL_3_1;
                    currentAnimation.play();
                });
                currentAnimation = LEVEL_3;
                currentAnimation.play();
            } else if (newLevel == 2) {
                Timeline LEVEL_2 = new Timeline(
                        new KeyFrame(Duration.ZERO, e -> { view.setImage(frame_03); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len), e -> { view.setImage(frame_04); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len * 2), e -> { view.setImage(frame_03); view.setTranslateX(offset); })
                );
                LEVEL_2.setCycleCount(Animation.INDEFINITE);
                currentAnimation = LEVEL_2;
                currentAnimation.play();
            } else if (newLevel == 1) {
                Timeline LEVEL_1 = new Timeline(
                        new KeyFrame(Duration.ZERO, e -> { view.setImage(frame_01); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len), e -> { view.setImage(frame_02); view.setTranslateX(offset+3); }),
                        new KeyFrame(Duration.millis(frame_len * 2), e -> { view.setImage(frame_01); view.setTranslateX(offset); })
                );
                LEVEL_1.setCycleCount(Animation.INDEFINITE);
                currentAnimation = LEVEL_1;
                currentAnimation.play();
            } else {
                Timeline LEVEL_0 = new Timeline(
                        new KeyFrame(Duration.ZERO, e -> { view.setImage(frame_13); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len), e -> { view.setImage(frame_14); view.setTranslateX(offset); }),
                        new KeyFrame(Duration.millis(frame_len * 2), e -> { view.setImage(frame_13); view.setTranslateX(offset); })
                );
                LEVEL_0.setCycleCount(15);
                LEVEL_0.setOnFinished(event -> {
                    Timeline LEVEL_0_1 = new Timeline(
                            new KeyFrame(Duration.ZERO, e -> { view.setImage(frame_15); view.setTranslateX(offset); }),
                            new KeyFrame(Duration.millis(frame_len), e -> { view.setImage(frame_16); view.setTranslateX(offset); }),
                            new KeyFrame(Duration.millis(frame_len * 2), e -> { view.setImage(frame_15); view.setTranslateX(offset); })
                    );
                    LEVEL_0_1.setCycleCount(Animation.INDEFINITE);
                    currentAnimation = LEVEL_0_1;
                    currentAnimation.play();
                });
                currentAnimation = LEVEL_0;
                currentAnimation.play();
            }
        }

        private void loadImages() {
            try {
                frame_01 = new Image("0.png");
                frame_02 = new Image("1.png");
                frame_03 = new Image("2.png");
                frame_04 = new Image("3.png");
                frame_05 = new Image("3-1.png");
                frame_06 = new Image("3-2.png");
                frame_07 = new Image("3-4.png");
                frame_08 = new Image("3-5.png");
                frame_09 = new Image("3-6.png");
                frame_10 = new Image("3-7.png");
                frame_11 = new Image("3-8.png");
                frame_12 = new Image("3-9.png");
                frame_13 = new Image("01.png");
                frame_14 = new Image("02.png");
                frame_15 = new Image("03.png");
                frame_16 = new Image("04.png");
            } catch (Exception e) {
                System.out.println("Image loading error: " + e.getMessage());
            }
        }

        @Override
        public void stop() throws Exception {
            if (rewardEngine != null) rewardEngine.shutdownScheduler();
            super.stop();
        }
}



