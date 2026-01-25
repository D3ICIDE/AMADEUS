package com.AMADEUS.phase;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

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

    // --- ADDED METHOD TO FIX ERROR ---
    // This bridges the gap between AMADEUS_PHASE_3 calling 'appendText'
    // and this class's logic.
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
        String htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>

            <link rel="stylesheet"
                  href="https://cdn.jsdelivr.net/npm/prismjs@1/themes/prism-tomorrow.min.css">
            <script src="https://cdn.jsdelivr.net/npm/prismjs@1/components/prism-core.min.js"></script>
            <script src="https://cdn.jsdelivr.net/npm/prismjs@1/plugins/autoloader/prism-autoloader.min.js"></script>

            <script src="https://cdn.jsdelivr.net/npm/prismjs@1/plugins/line-numbers/prism-line-numbers.min.js"></script>
            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/prismjs@1/plugins/line-numbers/prism-line-numbers.css">

            <script>
                Prism.plugins.autoloader.languages_path =
                    'https://cdn.jsdelivr.net/npm/prismjs@1/components/';
            </script>

            <link href="https://fonts.googleapis.com/css2?family=Fira+Code&display=swap" rel="stylesheet">

            <style>
                body {
                    font-family: 'Segoe UI', 'Roboto', 'Helvetica Neue', sans-serif;
                    background-color: #0A1931;
                    color: #F3F4F6;
                    margin: 0;
                    padding: 20px 20px 20px 10px;
                    overflow-y: auto;
                    overflow-x: hidden; 
                    text-rendering: optimizeLegibility;
                }

                #prompt-container {
                    display: none;
                    background-color: #0A1931;
                    border-left: 4px solid #3b82f6;
                    padding: 12px 16px;
                    border-radius: 6px;
                    margin-bottom: 24px;
                    box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                }
                .prompt-label {
                    font-size: 12px;
                    font-weight: bold;
                    color: #93c5fd;
                    margin-bottom: 4px;
                    letter-spacing: 1px;
                }
                #prompt-text {
                    font-size: 14px;
                    color: #E5E7EB;
                    white-space: pre-wrap;
                }

                h1, h2, h3 {
                    color: #FFFFFF;
                    border-bottom: 1px solid #374151;
                    padding-bottom: 5px;
                }
                p { line-height: 1.6; }
                strong { color: #FFFFFF; }

                /* Inline Code Styling */
                :not(pre) > code {
                    background-color: #222222;
                    font-style: italic;
                    padding: 2px 6px;
                    border-radius: 4px;
                    color: #FFFFFF;
                    font-family: 'Fira Code', 'Consolas', 'Monaco', 'Courier New', monospace;
                    font-size: 13px;
                }

                .code-block {
                    position: relative;
                    margin: 0 0 1rem 0;
                }

                .code-copy-btn {
                    position: absolute;
                    top: 6px;
                    right: 8px;
                    font-size: 11px;
                    padding: 2px 8px;
                    border-radius: 4px;
                    border: 1px solid #4b5563;
                    background-color: #111827;
                    color: #e5e7eb;
                    cursor: pointer;
                    opacity: 0.8;
                    outline: none;
                    user-select: none;
                    z-index: 30; 
                }
                .code-copy-btn:hover {
                    opacity: 1;
                    background-color: #1f2937;
                }

                /* --- MAIN CODE BLOCK STYLING --- */
                pre[class*="language-"] {
                    background-color: #111827 !important;
                    border-radius: 8px;
                    
                    /* CHANGED: Increased padding to push text further right */
                    padding: 10px 10px 10px 4.2em !important; 
                    
                    border: 1px solid #1f2937;
                    font-size: 13px;
                    font-family: 'Fira Code', 'Consolas', 'Monaco', monospace;
                    line-height: 1.5;
                    width: 100%;
                    box-sizing: border-box;
                    white-space: pre; 
                    word-break: normal;
                    overflow-x: auto; 
                    color: #e5e7eb;
                    position: relative; 
                }

                pre[class*="language-"] > code {
                    background-color: transparent !important;
                    font-family: 'Fira Code', 'Consolas', 'Monaco', monospace;
                    white-space: pre; 
                    word-break: normal;
                    display: block; 
                }
                
                /* --- LINE NUMBERS FIX --- */
                span.line-numbers-rows {
                    position: absolute !important;
                    top: 0 !important;
                    
                    /* CHANGED: Fits the new padding space */
                    left: -4.2em !important; 
                    
                    /* CHANGED: Kept width smaller than padding to create the visual gap */
                    width: 3.0em !important;
                    
                    background-color: #111827 !important; 
                    z-index: 20 !important;
                    
                    border-right: 1px solid #374151; 
                    
                    display: block !important;
                    height: 100% !important; 
                    pointer-events: none; 
                }
                
                span.line-numbers-rows > span {
                    display: block;
                    counter-increment: linenumber;
                }

                ::-webkit-scrollbar { width: 10px; height: 10px; }
                ::-webkit-scrollbar-track { background: #0A1931; }
                ::-webkit-scrollbar-thumb {
                    background: #4B5563;
                    border-radius: 5px;
                    border: 2px solid #172554;
                }
                ::-webkit-scrollbar-thumb:hover { background: #6366F1; }
            </style>
        </head>
        <body>
            <div id="prompt-container">
                <div class="prompt-label">YOUR PROMPT</div>
                <div id="prompt-text"></div>
            </div>

            <div id="content"></div>

            <script>
                var rawMarkdown = "";
                var contentDiv = document.getElementById('content');
                var promptContainer = document.getElementById('prompt-container');
                var promptTextDiv = document.getElementById('prompt-text');

                function setPrompt(text) {
                    promptTextDiv.textContent = text;
                    promptContainer.style.display = "block";
                }

                function enhanceCodeBlocks() {
                    var pres = contentDiv.querySelectorAll('pre[class*="language-"]');

                    pres.forEach(function(pre) {
                        // 1. Copy Button Logic
                        if (!pre.parentElement.classList.contains('code-block')) {
                            var wrapper = document.createElement('div');
                            wrapper.className = 'code-block';
                            pre.parentNode.insertBefore(wrapper, pre);
                            wrapper.appendChild(pre);

                            var btn = document.createElement('button');
                            btn.className = 'code-copy-btn';
                            btn.textContent = 'Copy';
                            
                            btn.addEventListener('click', function() {
                                var code = pre.querySelector('code');
                                var text = code ? code.textContent : pre.textContent;
                                if (navigator.clipboard && navigator.clipboard.writeText) {
                                    navigator.clipboard.writeText(text).then(function () {
                                        btn.textContent = 'Copied';
                                        setTimeout(function () { btn.textContent = 'Copy'; }, 1000);
                                    }, function () { btn.textContent = 'Failed'; setTimeout(function () { btn.textContent = 'Copy'; }, 1000); });
                                } else {
                                    var range = document.createRange(); range.selectNodeContents(pre);
                                    var sel = window.getSelection(); sel.removeAllRanges(); sel.addRange(range);
                                    document.execCommand('copy'); btn.textContent = 'Copied';
                                    setTimeout(function () { btn.textContent = 'Copy'; }, 1000);
                                    sel.removeAllRanges();
                                }
                            });
                            wrapper.appendChild(btn);
                        }
                        
                        // 2. Scroll Sync for Line Numbers
                        pre.onscroll = function() {
                            var rows = pre.querySelector('.line-numbers-rows');
                            if(rows) {
                                rows.style.transform = 'translateX(' + pre.scrollLeft + 'px)';
                            }
                        };
                    });
                }

                function appendContent(chunk) {
                    rawMarkdown += chunk;
                    contentDiv.innerHTML = marked.parse(rawMarkdown);

                    contentDiv.querySelectorAll('pre').forEach(function(pre) {
                        if (pre.querySelector('code')) {
                            pre.classList.add('line-numbers');
                        }
                    });

                    Prism.highlightAllUnder(contentDiv);
                    enhanceCodeBlocks();

                    window.scrollTo(0, document.body.scrollHeight);
                }

                function resetChat() {
                    contentDiv.innerHTML = "";
                    rawMarkdown = "";
                    promptTextDiv.textContent = "";
                    promptContainer.style.display = "none";
                }
            </script>
        </body>
        </html>
        """;
        webEngine.loadContent(htmlContent);
    }
}