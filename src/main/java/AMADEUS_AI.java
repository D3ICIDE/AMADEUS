import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import javafx.application.Platform;
import javafx.scene.control.Label;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class AMADEUS_AI implements Runnable {
    private final AMADEUS_UI mainApp;
    private final String detectedKeyword;
    public AMADEUS_AI(AMADEUS_UI mainApp, Label promptLabel, String detectedKeyword) {
        this.mainApp = mainApp;

        this.detectedKeyword = detectedKeyword; // Initialize the new field
    }
    static {
        try {
            if (!GraphicsEnvironment.isHeadless()) {
                Toolkit.getDefaultToolkit();
            }
        } catch (Exception e) {
            System.err.println("WARNING: Failed to initialize AWT Toolkit.");
        }
    }
    public String getClipboardContent() {
        String data = null;
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                data = (String) clipboard.getData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            System.err.println("Clipboard access failed: " + e.getMessage());
        }
        return data;
    }
    @Override
    public void run() {
        System.out.println("--- AI TASK STARTED ---");
        try {
            String apiKey = System.getenv("GEMINI_API_KEY");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                handleError("ERROR: GEMINI_API_KEY missing.");
                return;
            }

            String requestText = getClipboardContent();
            if (requestText == null || requestText.isBlank()) {
                handleError("Clipboard empty. Please copy code first.");
                return;
            }

            ResponseFormatter formatter = mainApp.getResponseFormatter();
            formatter.setPrompt(requestText);
            Client client = Client.builder().apiKey(apiKey).build();
            String modelName = getGeminiModelName();
            String systemPrompt = getSystemPrompt();

            System.out.println("Using Model: " + modelName);
            System.out.println("Using Context: " + detectedKeyword);

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .temperature(0.7f)
                    .maxOutputTokens(8000)
                    .build();

            String fullPrompt = systemPrompt + "\n\nCode to Analyze:\n" + requestText;

            System.out.println("Requesting Stream...");
            Iterable<GenerateContentResponse> responseStream = client.models.generateContentStream(
                    modelName,
                    fullPrompt,
                    config
            );

            boolean firstChunk = true;

            for (GenerateContentResponse chunk : responseStream) {
                String textChunk = chunk.text();

                if (textChunk != null && !textChunk.isEmpty()) {
                    if (firstChunk) {
                        // --- 4. STOP ANIMATION ON FIRST DATA ---
                        Platform.runLater(mainApp::hideThinking);
                        firstChunk = false;
                    }

                    formatter.append(textChunk);
                }
            }

            formatter.finish();

            System.out.println("\n--- AMADEUS AI ANALYSIS COMPLETE ---\n");

        } catch (Exception e) {
            e.printStackTrace();
            handleError("CRITICAL FAILURE: " + e.getMessage());
        }
    }

    private String getGeminiModelName() {
        return switch (detectedKeyword) {
            case "Command Arise" -> "gemini-3.1-flash-lite"; // Speed
            case "Command Ascend" -> "gemini-3.6-flash";  // Balance
            case "Command Absolute" -> "gemma-4-26b-a4b-it"; // Quality
            default -> "gemini-3.1-flash-lite";
        };
    }

    private String getSystemPrompt() {
        return switch (detectedKeyword) {
            case "Command Arise" -> """
                You are AMADEUS, a strict AI code mentor. You will receive a block of raw code.
                **Your Goal:** Identify the most critical issues and provide deep conceptual reasoning. Do NOT provide the code fix.
                
                **Protocol (STRICTLY FOLLOW):**
                1.  **DIAGNOSE & PRIORITIZE:** Scan for Critical Errors (Syntax/Runtime) or Logical Flaws.
                2.  **IF CODE IS BROKEN:**
                    * **Identify:** State the Line Number.
                    * **Deep Reasoning:** Explain the execution flow error.
                    * **No Solutions:** **NEVER** write the fixed code.
                3.  **IF CODE SEEMS CORRECT:** Challenge the user with an edge case.
                
                **Output Format:**
                * **Status:** `[CRITICAL / WARNING]`
                * **Analysis:** (Bullet points with deep reasoning)
                * **Hint:** (One single, actionable sentence).
                """;

            case "Command Ascend" -> """
                You are AMADEUS, an AI Mentor providing a detailed conceptual guide.
                **Your Goal:** Provide a comprehensive explanation of HOW to fix the issue without writing the code for
                 the user.
                
                **Protocol:**
                1. **DIAGNOSE:** Identify the error clearly.
                2. **EXPLAIN:** Provide deep conceptual reasoning for why it fails.
                3. **GUIDE:** Explain the exact logic, function, or design pattern that must be used to fix the code.
                 You can write pseudo-code or small snippets, but DO NOT write the full corrected block.
                
                **Output Format:**
                * **Status:** `[DETAILED GUIDE]`
                * **Deep Analysis:** (Explain the concepts involved)
                * **Implementation Guide:** (Step-by-step instructions on how to write the fix).
                """;

            case "Command Absolute" -> """
                You are AMADEUS, providing a last-resort, complete solution.
                **Your Goal:** Fix all issues and provide the working code.
                
                **Protocol:**
                1. **DIAGNOSE:** Identify ALL errors.
                2. **FIXES:** List the line numbers of the fixes and briefly explain what was changed.
                3. **SOLUTION:** Provide the **FULL, CORRECTED CODE BLOCK**.
                
                **Output Format:**
                * **Status:** `[ABSOLUTE SOLUTION]`
                * **Fix Log:** (List of changes)
                * **Solution:** (The full, corrected code block).
                """;

            default -> "You are a helpful AI assistant.";
        };
    }

    private void handleError(String message) {
        Platform.runLater(() -> {
            mainApp.hideThinking();
            ResponseFormatter formatter = mainApp.getResponseFormatter();
            if (formatter != null) {
                formatter.append("\n\n**SYSTEM ERROR:** " + message);
                formatter.finish();
            }
        });
    }
}