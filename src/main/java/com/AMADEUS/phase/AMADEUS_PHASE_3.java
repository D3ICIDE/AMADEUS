package com.AMADEUS.phase;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

import java.util.concurrent.atomic.AtomicInteger;

public class AMADEUS_PHASE_3 extends Application {
    public static final AtomicInteger point = new AtomicInteger(500);
    private static final int ACTIVATION_COST = 90;
    private RewardManager rewardEngine;
    private Popup popup;
    private ImageView view;
    private Image frame_01, frame_02, frame_03, frame_04, frame_05, frame_06,
            frame_07, frame_08, frame_09, frame_10, frame_11, frame_12, frame_13, frame_14, frame_15, frame_16;
    private Timeline currentAnimation;
    private int currentLevel = 0;

    private static AMADEUS_PHASE_3 instance;
    private Label promptLabel;

    private VBox responseContainer;

    private Label scoreDisplay;
    private Label levelNotification;

    private Stage primaryStage;
    final double POPUP_WIDTH = 550;
    final double POPUP_HEIGHT = 550;

    public static void main(String[] args) {
        launch(args);
    }

    public static AMADEUS_PHASE_3 getInstance() {
        return instance;
    }


    // Calls the compartmentalized helper class
    private void updateResponseText(String rawText) {
        ResponseFormatter.format(responseContainer, rawText);
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

    // LEVEL NOTIFICATION LOGIC
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

    public void safeUpdateUI(int currentPoint) {
        Platform.runLater(() -> {
            updateAnimationTimeline(currentPoint);
            if(scoreDisplay != null) scoreDisplay.setText("EXP: " + currentPoint);
        });
    }

    public void triggerAnalysis() {
        Platform.runLater(() -> {
            if (responseContainer == null && promptLabel == null && primaryStage == null && popup == null) {
                System.err.println("Error: UI components were not fully initialized.");
                return;
            }
            if (currentLevel == 0) {
                System.out.println("System has fallen below the minimum threshold.");
                return;
            }
            int currentPoints = point.addAndGet(-ACTIVATION_COST);
            safeUpdateUI(currentPoints);

            this.popup.show(primaryStage);

            updateResponseText("Trigger received. Accessing clipboard...");


            AMADEUS_PHASE_2 logic = new AMADEUS_PHASE_2(this, promptLabel);

            Thread logicThread = new Thread(logic, "AMADEUS-AI-Processor");
            logicThread.setDaemon(true);
            logicThread.start();
        });
    }

    // Public method for Phase 2 to call
    public void displayAIResponse(String text) {
        updateResponseText(text);
    }

    public void start(Stage worldLine) throws Exception {
        instance = this;
        this.primaryStage = worldLine;

        // --- CORRECT IMAGE LOADING ---
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
            System.out.println("Image loading error (Check paths): " + e.getMessage());
        }

        this.rewardEngine = new RewardManager(this, point);
        rewardEngine.startPointIncrementer();

        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: black;");
        Scene theatre = new Scene(root, Color.BLACK);

        view = new ImageView(frame_01);
        view.setFitWidth(200);
        view.setFitHeight(250);
        view.setTranslateX(25);
        view.setX(0.0);
        view.setY(0.0);
        view.preserveRatioProperty().set(true);

        scoreDisplay = new Label("EXP: " + point.get());
        scoreDisplay.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        scoreDisplay.setStyle("-fx-text-fill: #00FFFF; -fx-effect: dropshadow(one-pass-box, darkgreen, 4, 0.5, 0, 0);");
        scoreDisplay.setTranslateY(10);

        levelNotification = new Label("");
        levelNotification.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        levelNotification.setOpacity(0);
        levelNotification.setTranslateY(30);

        root.getChildren().addAll(view, scoreDisplay, levelNotification);
        StackPane.setAlignment(scoreDisplay, Pos.TOP_LEFT);
        StackPane.setAlignment(levelNotification, Pos.TOP_LEFT);

        promptLabel = new Label("Copy code and relaunch to see the prompt.");
        promptLabel.setWrapText(true);
        promptLabel.setStyle("-fx-text-fill: white; -fx-font-family: 'Verdana'; -fx-font-size: 14px; -fx-background-color: #0A1931; -fx-padding: 10;");


        responseContainer = new VBox();
        responseContainer.setStyle("-fx-background-color: #0A1931; -fx-padding: 10;");

        updateResponseText("Awaiting command...");


        updateAnimationTimeline(point.get());

        AMADEUS_VOICE_RECOG listener = new AMADEUS_VOICE_RECOG(this.instance);
        Thread listenerThread = new Thread(listener, "AMADEUS-Voice-Listener");
        listenerThread.setDaemon(true);
        listenerThread.start();

        worldLine.setTitle("AMADEUS");
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        worldLine.setX(screenBounds.getMaxX() - 200);
        worldLine.setY(screenBounds.getMaxY() - 250);
        worldLine.setWidth(200);
        worldLine.setHeight(250);
        worldLine.setScene(theatre);
        worldLine.setAlwaysOnTop(true);
        worldLine.setResizable(false);

        this.popup = new Popup();
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 3 8 3 8;");
        closeButton.setOnAction(e -> this.popup.hide());

        Slider slider = new Slider(0, 1, 0);
        slider.setOrientation(Orientation.VERTICAL);
        slider.setId("modern-scroll-slider");
        slider.setScaleY(-1);

        BorderPane header = new BorderPane();
        Label title = new Label("Prompt/Settings");
        title.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        header.setLeft(title);
        header.setRight(closeButton);

        // Unified Layout
        VBox conversationBox = new VBox(10);
        conversationBox.setStyle("-fx-background-color: transparent;");
        conversationBox.getChildren().addAll(promptLabel, responseContainer);

        ScrollPane masterScrollPane = new ScrollPane(conversationBox);
        masterScrollPane.setFitToWidth(true); // Tells content to stretch

        masterScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        masterScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        masterScrollPane.setStyle("-fx-background: #0A1931; -fx-background-color: #0A1931; -fx-border-color: transparent;");


        conversationBox.prefWidthProperty().bind(masterScrollPane.widthProperty().subtract(2));


        conversationBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            masterScrollPane.layout(); // Force layout update first
            masterScrollPane.setVvalue(1.0); // Scroll to bottom
        });

        HBox contentArea = new HBox(5);
        contentArea.getChildren().addAll(masterScrollPane, slider);
        HBox.setHgrow(masterScrollPane, Priority.ALWAYS);


        slider.valueProperty().bindBidirectional(masterScrollPane.vvalueProperty());

        VBox popupContent = new VBox(5);
        popupContent.getChildren().addAll(header, contentArea);
        VBox.setVgrow(contentArea, Priority.ALWAYS);

        popupContent.setStyle("-fx-background-color: #0A1931; -fx-border-color: #b39595; -fx-border-width: 2; -fx-padding: 10;");
        popupContent.setPrefSize(POPUP_WIDTH, POPUP_HEIGHT);

        this.popup.setX(screenBounds.getMaxX() - POPUP_WIDTH);
        this.popup.setY(screenBounds.getMinY());

        try { popupContent.getStylesheets().add(getClass().getResource("/slider.css").toExternalForm()); } catch(Exception e) {}
        popup.getContent().add(popupContent);

        worldLine.show();

        AnimationTimer scoreSync = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(scoreDisplay != null) scoreDisplay.setText("EXP: " + point.get());
            }
        };
        scoreSync.start();
    }

    @Override
    public void stop() throws Exception {
        if (rewardEngine != null) rewardEngine.shutdownScheduler();
        super.stop();
    }
}