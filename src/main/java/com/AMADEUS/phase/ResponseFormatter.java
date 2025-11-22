package com.AMADEUS.phase;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResponseFormatter {


    private static final Pattern PATTERN = Pattern.compile("```(.*?)```|\\*\\*(.*?)\\*\\*|`([^`]+)`", Pattern.DOTALL);


    private static final int TYPE_DELAY = 15;

    public static void format(VBox container, String rawText) {
        Platform.runLater(() -> {
            container.getChildren().clear();


            Text header = new Text("AMADEUS AI:\n");
            header.setStyle("-fx-fill: #00FFFF; -fx-font-weight: bold; -fx-font-family: 'Verdana';");
            TextFlow headerFlow = new TextFlow(header);
            headerFlow.setPadding(new Insets(0, 0, 10, 0));
            container.getChildren().add(headerFlow);


            Queue<Runnable> animationQueue = new LinkedList<>();
            Matcher matcher = PATTERN.matcher(rawText);
            int lastIndex = 0;

            final TextFlow[] currentFlowWrapper = { new TextFlow() };
            currentFlowWrapper[0].setPadding(new Insets(0, 0, 10, 0));


            container.getChildren().add(currentFlowWrapper[0]);

            while (matcher.find()) {
                String plainTextPart = rawText.substring(lastIndex, matcher.start());

                // Task: Type Plain Text
                if (!plainTextPart.isEmpty()) {
                    TextFlow targetFlow = currentFlowWrapper[0];
                    animationQueue.add(() -> typeText(targetFlow, plainTextPart, false, animationQueue));
                }

                String blockCode = matcher.group(1);
                String boldText = matcher.group(2);
                String inlineCode = matcher.group(3);

                if (blockCode != null) {

                    animationQueue.add(() -> {

                        Node codeBlock = createCodeBlock(blockCode);
                        codeBlock.setOpacity(0); // Start invisible
                        container.getChildren().add(codeBlock);


                        TextFlow nextFlow = new TextFlow();
                        nextFlow.setPadding(new Insets(0, 0, 10, 0));
                        container.getChildren().add(nextFlow);
                        currentFlowWrapper[0] = nextFlow; // Update reference


                        FadeTransition ft = new FadeTransition(Duration.millis(300), codeBlock);
                        ft.setFromValue(0);
                        ft.setToValue(1);
                        ft.setOnFinished(e -> runNextTask(animationQueue));
                        ft.play();
                    });

                } else if (boldText != null) {

                    TextFlow targetFlow = currentFlowWrapper[0];
                    animationQueue.add(() -> typeText(targetFlow, boldText, true, animationQueue));

                } else if (inlineCode != null) {

                    TextFlow targetFlow = currentFlowWrapper[0];
                    animationQueue.add(() -> {
                        Label codeLbl = createInlineCode(inlineCode);
                        targetFlow.getChildren().add(codeLbl);
                        // Small delay to feel natural
                        new Timeline(new KeyFrame(Duration.millis(50), e -> runNextTask(animationQueue))).play();
                    });
                }

                lastIndex = matcher.end();
            }

            // Task: Type Remaining Text
            if (lastIndex < rawText.length()) {
                String remaining = rawText.substring(lastIndex);
                TextFlow targetFlow = currentFlowWrapper[0];
                animationQueue.add(() -> typeText(targetFlow, remaining, false, animationQueue));
            }


            runNextTask(animationQueue);
        });
    }


    private static void runNextTask(Queue<Runnable> queue) {
        Runnable next = queue.poll();
        if (next != null) {
            next.run();
        }
    }


    private static void typeText(TextFlow container, String content, boolean isBold, Queue<Runnable> nextTasks) {
        Text textNode = new Text(""); // Start empty
        if (isBold) {
            textNode.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
            textNode.setStyle("-fx-fill: white;");
        } else {
            textNode.setFont(Font.font("Verdana", 14));
            textNode.setStyle("-fx-fill: #b0c4de;");
        }
        container.getChildren().add(textNode);


        StringBuilder sb = new StringBuilder();
        char[] chars = content.toCharArray();

        Timeline timeline = new Timeline();

        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            // Add a frame for every character
            timeline.getKeyFrames().add(
                    new KeyFrame(Duration.millis((i + 1) * TYPE_DELAY), e -> {
                        sb.append(c);
                        textNode.setText(sb.toString());
                    })
            );
        }


        timeline.setOnFinished(e -> runNextTask(nextTasks));
        timeline.play();
    }

    
    private static Label createInlineCode(String text) {
        Label codeLabel = new Label(" " + text + " ");
        codeLabel.setStyle(
                "-fx-font-family: 'Consolas', 'Monospaced';" +
                        "-fx-text-fill: #FFA500;" +
                        "-fx-background-color: #2a2a2a;" +
                        "-fx-background-radius: 4;" +
                        "-fx-font-size: 13px;"
        );
        return codeLabel;
    }

    private static AnchorPane createCodeBlock(String codeContent) {
        Label codeLabel = new Label(codeContent.trim());
        codeLabel.setStyle(
                "-fx-font-family: 'Consolas', 'Monospaced';" +
                        "-fx-text-fill: #00ff00;" +
                        "-fx-background-color: #000000;" +
                        "-fx-padding: 15 15 15 15;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-color: #333;" +
                        "-fx-border-width: 1;"
        );
        codeLabel.setWrapText(true);
        codeLabel.setMaxWidth(Double.MAX_VALUE);
        codeLabel.setMinHeight(40);

        Button copyBtn = new Button("COPY");
        copyBtn.setFont(Font.font("Verdana", FontWeight.BOLD, 10));
        copyBtn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: #888; -fx-background-radius: 3; -fx-cursor: hand;");

        copyBtn.setOnAction(e -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(codeContent.trim());
            clipboard.setContent(content);

            copyBtn.setText("COPIED!");
            copyBtn.setStyle("-fx-background-color: rgba(0,255,0,0.2); -fx-text-fill: #00FF00;");

            new Thread(() -> {
                try { Thread.sleep(2000); } catch (InterruptedException ex) {}
                Platform.runLater(() -> {
                    copyBtn.setText("COPY");
                    copyBtn.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: #888;");
                });
            }).start();
        });

        AnchorPane codeBlockPane = new AnchorPane();
        codeBlockPane.getChildren().addAll(codeLabel, copyBtn);

        AnchorPane.setTopAnchor(codeLabel, 0.0);
        AnchorPane.setBottomAnchor(codeLabel, 0.0);
        AnchorPane.setLeftAnchor(codeLabel, 0.0);
        AnchorPane.setRightAnchor(codeLabel, 0.0);
        AnchorPane.setTopAnchor(copyBtn, 5.0);
        AnchorPane.setRightAnchor(copyBtn, 5.0);

        VBox.setMargin(codeBlockPane, new Insets(5, 0, 15, 0));
        return codeBlockPane;
    }
}