import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class AvatarConfig {

    private int testCommandIndex = 0;
    private final String[] TEST_COMMANDS = {
            "Command Arise",
            "Command Ascend",
            "Command Absolute"
    };

    // --- ANIMATION ASSETS ---
    public Image frame_01, frame_02, frame_03, frame_04, frame_05, frame_06,
            frame_07, frame_08, frame_09, frame_10, frame_11, frame_12,
            frame_13, frame_14, frame_15, frame_16, Icon;

    void setupCharacterView(StackPane root) {
        Ui.view = new ImageView(frame_01);
        Ui.view.setFitWidth(200);
        Ui.view.setFitHeight(250);
        Ui.view.setTranslateX(25);
        Ui.view.preserveRatioProperty().set(true);

        Ui.scoreDisplay = new Label("EXP: " + Ui.point.get());
        Ui.scoreDisplay.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
        Ui.scoreDisplay.setStyle("-fx-text-fill: #00FFFF; -fx-effect: dropshadow(one-pass-box, darkgreen, 4, 0.5, 0, 0);");
        Ui.scoreDisplay.setTranslateY(10);

        Ui.levelNotification = new Label("");
        Ui.levelNotification.setFont(Font.font("Verdana", FontWeight.BOLD, 12));
        Ui.levelNotification.setOpacity(0);
        Ui.levelNotification.setTranslateY(30);

        // --- TEST BUTTON ---
        Button testButton = new Button("TEST: " + TEST_COMMANDS[0].replace("Command ", ""));
        testButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00FFFF; -fx-border-color: #00FFFF; -fx-border-radius: 5; -fx-font-size: 10px; -fx-cursor: hand;");

        testButton.setOnAction(e -> {
            String commandToTest = TEST_COMMANDS[testCommandIndex];

            // FIX: Use the active singleton instance instead of 'new AMADEUS_UI()'
            Ui uiObj = Ui.getInstance();
            if (uiObj != null) {
                uiObj.triggerAnalysis(commandToTest);
            }

            testCommandIndex = (testCommandIndex + 1) % TEST_COMMANDS.length;

            String nextCmdName = TEST_COMMANDS[testCommandIndex].replace("Command ", "");
            testButton.setText("TEST: " + nextCmdName);
        });

        testButton.setOnMouseEntered(e -> testButton.setStyle("-fx-background-color: #00FFFF; -fx-text-fill: black; -fx-border-color: #00FFFF; -fx-border-radius: 5; -fx-font-size: 10px; -fx-cursor: hand;"));
        testButton.setOnMouseExited(e -> testButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #00FFFF; -fx-border-color: #00FFFF; -fx-border-radius: 5; -fx-font-size: 10px; -fx-cursor: hand;"));

        root.getChildren().addAll(Ui.view, Ui.scoreDisplay, Ui.levelNotification, testButton);
        StackPane.setAlignment(Ui.scoreDisplay, Pos.TOP_LEFT);
        StackPane.setAlignment(Ui.levelNotification, Pos.TOP_LEFT);
        StackPane.setAlignment(testButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(testButton, new javafx.geometry.Insets(10));
    }

    void loadImages() {
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
            Icon     = new Image("retro_512.png");
        } catch (Exception e) {
            System.out.println("Image loading error: " + e.getMessage());
        }
    }
}