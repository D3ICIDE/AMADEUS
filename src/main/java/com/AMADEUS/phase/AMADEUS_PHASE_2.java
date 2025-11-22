package com.AMADEUS.phase;

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

public class AMADEUS_PHASE_2 implements Runnable {
    // CHANGED: We now hold a reference to the main app instead of the response label
    private final AMADEUS_PHASE_3 mainApp;
    private final Label promptLabel;

    // CHANGED: Constructor now accepts the main app instance
    public AMADEUS_PHASE_2(AMADEUS_PHASE_3 mainApp, Label promptLabel) {
        this.mainApp = mainApp;
        this.promptLabel = promptLabel;
    }

    static {
        try {
            if (!GraphicsEnvironment.isHeadless()) {
                Toolkit.getDefaultToolkit();
            }
        } catch (Exception e) {
            System.err.println("WARNING: Failed to initialize AWT Toolkit. Clipboard access may fail.");
        }
    }

    public String ClipBoardContent(){
        String Request = null;
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
                Request = (String) clipboard.getData(DataFlavor.stringFlavor);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            System.err.println("Clipboard access failed: " + e.getMessage());
        }
        return Request;
    }

    public String GenAI(String Request) {
        // Ensure your GEMINI_API_KEY is set in your Environment Variables
        Client AMADEUS = Client.builder()
                .apiKey(System.getenv("GEMINI_API_KEY"))
                .build();

        GenerateContentConfig config = GenerateContentConfig.builder()
                .temperature(0.5f)
                .maxOutputTokens(8000)
                .topP(0.5f)
                .build();

        GenerateContentResponse response = AMADEUS.models.generateContent(
                "gemini-2.0-flash", // Updated to newer model if available, or keep 1.5-flash
                Request,
                config);
        return response.text();
    }

    @Override
    public void run() {
        System.out.println("--- AI TASK STARTED ---");

        try {
            String apiKey = System.getenv("GEMINI_API_KEY");
            if (apiKey == null || apiKey.trim().isEmpty()) {
                // CHANGED: Use mainApp to display error
                mainApp.displayAIResponse("ERROR: GEMINI_API_KEY environment variable is missing.");
                System.err.println("ERROR: GEMINI_API_KEY missing.");
                return;
            }

            String request = ClipBoardContent();
            if (request == null) {
                mainApp.displayAIResponse("Clipboard is empty. Please copy text and say 'Arise'.");
                return;
            }

            String context = "You're going to receive a code snippet or request. Analyze it for potential bugs, suggest improvements, keep the word count under 1000 words. If the code has no major flaws just say that. Format your response with Markdown: use **bold** for headers and ``` for code blocks.";
            String prompt = context + " Snippet/Request: " + request;

            // Update UI - Prompt is safe to set directly on Label inside runLater
            Platform.runLater(() -> promptLabel.setText("PROMPT: \n" + request));

            // Update UI - Response goes through the Rich Text Formatter in Phase 3
            mainApp.displayAIResponse("Analyzing data stream...");

            // Call AI
            String response = GenAI(prompt);

            // Display Final Response
            // Note: displayAIResponse handles Platform.runLater internally in Phase 3
            mainApp.displayAIResponse(response);

            System.out.println("\n--- AMADEUS AI ANALYSIS COMPLETE ---\n");

        } catch (Exception e) {
            System.err.println("Error during execution: " + e.getMessage());
            e.printStackTrace();
            mainApp.displayAIResponse("CRITICAL FAILURE: " + e.getMessage());
        }
    }
}