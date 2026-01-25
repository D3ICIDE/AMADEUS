package com.AMADEUS.phase;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class ThinkingIndicator extends HBox {

    private FadeTransition fade;
    private RotateTransition rotate;

    public ThinkingIndicator() {
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
        this.setPadding(new javafx.geometry.Insets(10, 0, 10, 0));

        // 1. Sci-Fi Spinner (A rotating square inside a circle)
        StackPane spinner = createSpinner();

        // 2. "Thinking" Text
        Label text = new Label("ANALYZING DATA STREAM...");
        text.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        text.setStyle("-fx-text-fill: #00FFFF;"); // Cyan color

        // 3. Pulse Animation for Text
        fade = new FadeTransition(Duration.seconds(0.8), text);
        fade.setFromValue(1.0);
        fade.setToValue(0.3);
        fade.setAutoReverse(true);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.play();

        this.getChildren().addAll(spinner, text);
    }

    private StackPane createSpinner() {
        StackPane stack = new StackPane();

        // Outer Ring
        Circle ring = new Circle(8);
        ring.setStroke(Color.web("#00FFFF"));
        ring.setStrokeWidth(2);
        ring.setFill(Color.TRANSPARENT);

        // Inner Rotating Square
        Rectangle square = new Rectangle(8, 8);
        square.setFill(Color.web("#00FFFF"));

        rotate = new RotateTransition(Duration.seconds(1.5), square);
        rotate.setByAngle(360);
        rotate.setCycleCount(Animation.INDEFINITE);
        rotate.play();

        stack.getChildren().addAll(ring, square);
        return stack;
    }
    public void start() {
        this.setVisible(true);
        fade.playFromStart();
        rotate.playFromStart();
    }

    public void stop() {
        fade.stop();
        rotate.stop();
    }
}