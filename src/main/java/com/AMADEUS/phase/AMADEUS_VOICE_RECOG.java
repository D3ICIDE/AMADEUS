package com.AMADEUS.phase;

import ai.picovoice.porcupine.*;
import javax.sound.sampled.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.List;

/**
 * FIXED:
 * 1. Removed Thread.sleep() which causes Microphone Buffer Overflow.
 * 2. Added explicit checks for file existence.
 * 3. Added volume level debugging to ensure Mic is actually hearing audio.
 */
public class AMADEUS_VOICE_RECOG implements Runnable {
    private final AMADEUS_PHASE_3 uiInstance;
    private static final String ACCESS_KEY = System.getenv("PV_API_KEY");

    // --- Define Keyword Paths ---
 
    private static final String KEYWORD_PATH_ARISE = "Command-Arise_en_windows_v3_0_0.ppn";
    private static final String KEYWORD_PATH_ASCEND = "Command-Ascend_en_windows_v3_0_0.ppn";
    private static final String KEYWORD_PATH_ABSOLUTE = "Command-Absolute_en_windows_v3_0_0.ppn";

    private static final List<String> KEYWORD_NAMES = Arrays.asList(
            "Command Arise",
            "Command Ascend",
            "Command Absolute"
    );

    // Cooldown management
    private long lastTriggerTime = 0;
    private static final long COOLDOWN_MS = 25000; // 25 seconds

    public AMADEUS_VOICE_RECOG(AMADEUS_PHASE_3 uiInstance) {
        this.uiInstance = uiInstance;
    }

    @Override
    public void run() {
        // 1. Validate Environment
        if (ACCESS_KEY == null || ACCESS_KEY.isEmpty()) {
            System.err.println("CRITICAL ERROR: PV_API_KEY is missing from Environment Variables.");
            return;
        }

        // 2. Validate Files
        String[] keywordPaths = {KEYWORD_PATH_ARISE, KEYWORD_PATH_ASCEND, KEYWORD_PATH_ABSOLUTE};
        for (String path : keywordPaths) {
            File f = new File(path);
            if (!f.exists()) {
                System.err.println("CRITICAL ERROR: Keyword file not found at: " + path);
                return;
            }
        }

        Porcupine porcupine = null;
        TargetDataLine micDataLine = null;

        try {
            // 3. Initialize Porcupine
            System.out.println("Initializing Porcupine...");
            porcupine = new Porcupine.Builder()
                    .setAccessKey(ACCESS_KEY)
                    .setKeywordPaths(keywordPaths)
                    .setSensitivities(new float[]{0.5f, 0.5f, 0.5f})
                    .build();

            // 4. Initialize Microphone
            // Use Porcupine's required sample rate (usually 16000Hz)
            AudioFormat format = new AudioFormat(porcupine.getSampleRate(), 16, 1, true, false);
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(dataLineInfo)) {
                System.err.println("CRITICAL ERROR: Microphone audio format not supported.");
                return;
            }

            micDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            micDataLine.open(format);
            micDataLine.start();

            int frameLength = porcupine.getFrameLength();
            ByteBuffer captureBuffer = ByteBuffer.allocate(frameLength * 2);
            captureBuffer.order(ByteOrder.LITTLE_ENDIAN);
            short[] porcupineBuffer = new short[frameLength];

            System.out.println("Status: AMADEUS is Listening. (Cooldown system active)");

            // 5. Main Loop
            while (true) {
                // Read bytes from mic
                int numBytesRead = micDataLine.read(captureBuffer.array(), 0, captureBuffer.capacity());

                if (numBytesRead != frameLength * 2) {
                    // Start of stream or glitch
                    continue;
                }

                // Convert bytes to shorts
                captureBuffer.asShortBuffer().get(porcupineBuffer);

            

                // Process audio
                int keywordIndex = porcupine.process(porcupineBuffer);

                if (keywordIndex >= 0) {
                    long currentTime = System.currentTimeMillis();

                    // Check Cooldown
                    if (currentTime - lastTriggerTime < COOLDOWN_MS) {
                        System.out.println("Wake word ignored (Cooling down): " + KEYWORD_NAMES.get(keywordIndex));
                    } else {
                        // Activate
                        String detectedKeyword = KEYWORD_NAMES.get(keywordIndex);
                        System.out.println(">>> AMADEUS ACTIVATED: " + detectedKeyword);

                        // Update cooldown timer
                        lastTriggerTime = currentTime;

                        // Trigger UI
                        if (uiInstance != null) {
                            uiInstance.triggerAnalysis(detectedKeyword);
                        }
                    }
                }
            }

        } catch (PorcupineException e) {
            System.err.println("Porcupine Error (Check API Key or .ppn file version): " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Microphone Error (Line Unavailable): " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (micDataLine != null) {
                micDataLine.stop();
                micDataLine.close();
            }
            if (porcupine != null) {
                porcupine.delete();
            }
        }
    }

    // Helper to see if Mic is working
    private void debugAudioVolume(short[] buffer) {
        int max = 0;
        for (short s : buffer) max = Math.max(max, Math.abs(s));
        if (max > 500) { // Only print if there is actual noise
            System.out.println("Mic Hearing Audio. Level: " + max);
        }
    }
}
