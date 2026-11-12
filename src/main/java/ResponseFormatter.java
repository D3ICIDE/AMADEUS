import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.MissingResourceException;

public class ResponseFormatter extends BorderPane {

    private final WebView webView;
    private final WebEngine webEngine;
    private String pendingPrompt = null; // Buffer for prompt if WebView isn't ready
    private boolean isReady = false;
    private Runnable onPromptDisplayed;

    public ResponseFormatter() {
        webView = new WebView();
        webEngine = webView.getEngine();

        // Keep WebView at 1.0 scaling
        webView.setZoom(1.0);
        webView.setFontScale(1.0);
        webView.setScaleX(1.0);
        webView.setScaleY(1.0);

        // Transparent to blend with JavaFX background
        webView.setPageFill(javafx.scene.paint.Color.TRANSPARENT);

        // Optional: request gray font smoothing for this node
        webView.setFontSmoothingType(javafx.scene.text.FontSmoothingType.GRAY);

        loadContent();

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                isReady = true;

                // 1. Flush Prompt if pending
                if (pendingPrompt != null) {
                    executeSetPrompt(pendingPrompt);
                    pendingPrompt = null;
                }
            }
        });

        // Help avoid subpixel layout issues
        setSnapToPixel(true);

        this.setCenter(webView);
    }

    public void setOnPromptDisplayed(Runnable action) {
        this.onPromptDisplayed = action;
    }

    // Method to set the User Prompt inside HTML
    public void setPrompt(String promptText) {
        Platform.runLater(() -> {
            if (isReady) {
                executeSetPrompt(promptText);
            } else {
                pendingPrompt = promptText;
            }
        });
    }


    public void appendText(String text) {
        // We simply delegate to append(), treating the text as a markdown chunk.
        append(text);
    }

    public void append(String chunk) {
        Platform.runLater(() -> {
            if (isReady) {
                executeAppend(chunk);
            }
            // If not ready, content logic would ideally buffer here,
            // but for simplicity we rely on the engine being ready quickly.
        });
    }

    /**
     * Signals the completion of the streaming process.
     */
    public void finish() {
        Platform.runLater(() -> {
            if (isReady) {
                // If any final actions are needed in the UI on stream end, they go here.
            }
        });
    }

    public void clear() {
        Platform.runLater(() -> {
            pendingPrompt = null;
            if (isReady) {
                // Clear Prompt AND Content
                webEngine.executeScript("resetChat();");
            } else {
                loadContent();
            }
        });
    }

    private void executeSetPrompt(String text) {
        String safeText = escapeString(text);
        webEngine.executeScript("setPrompt(\"" + safeText + "\");");
        if (onPromptDisplayed != null) {
            onPromptDisplayed.run();
        }
    }

    private void executeAppend(String text) {
        String safeText = escapeString(text);
        webEngine.executeScript("appendContent(\"" + safeText + "\");");
    }

    private String escapeString(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
    private void loadContent() {
        try {
            URL url = getClass().getResource("/webUI/index.html");
            assert url != null;
            webEngine.load(url.toExternalForm());
        }catch (MissingResourceException e){
            System.out.println("Missing resource URL for webview");
        }
    }
}