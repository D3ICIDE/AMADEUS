package com.AMADEUS.phase;

import ai.picovoice.porcupine.*;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
/**
 *  Replacement for AMADEUS_PHASE_1.java.
 *  This class is used for listening for voice trigger using PICOVOICE Porcupine
 * @author Vaibhav
 * @since 2025
 * @version 0.4
 *
 *
 *
 */

public class AMADEUS_VOICE_RECOG implements Runnable {
    private final AMADEUS_PHASE_3 uiInstance;
    private static final String ACCESS_KEY = System.getenv("PV_API_KEY");


    public AMADEUS_VOICE_RECOG(AMADEUS_PHASE_3 uiInstance) {
        this.uiInstance = uiInstance;
    }


    @Override
    public void run() {
        Porcupine porcupine = null;
        TargetDataLine micDataLine = null;

        try {

            porcupine = new Porcupine.Builder()
                    .setAccessKey(ACCESS_KEY)
                    .setKeywordPath("C:\\Users\\vaibh\\Downloads\\AMADEUS\\Command-Arise_en_windows_v3_0_0\\Command-Arise_en_windows_v3_0_0.ppn")
                    .setSensitivity(0.5f).build();

            AudioFormat format = new AudioFormat(porcupine.getSampleRate(),
                    16, 1, true, false);

            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);
            micDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            micDataLine.open(format);
            micDataLine.start();


            int frameLength = porcupine.getFrameLength();
            short[] porcupineBuffer = new short[frameLength];
            ByteBuffer captureBuffer = ByteBuffer.allocate(frameLength * 2);
            captureBuffer.order(ByteOrder.LITTLE_ENDIAN);

            System.out.println("Status: Listening for wake word...");

            while (true) {

                int numBytesRead = micDataLine.read(captureBuffer.array(), 0, captureBuffer.capacity());


                if (numBytesRead != frameLength * 2) {
                    continue;
                }


                captureBuffer.asShortBuffer().get(porcupineBuffer);


                int keywordIndex = porcupine.process(porcupineBuffer);

                if (keywordIndex >= 0) {
                    // Wake word detected!
                    System.out.println("AMADEUS Activated....");

                    // Trigger UI
                    uiInstance.triggerAnalysis();

                    // Pause listening for 25 seconds
                    try {
                        Thread.sleep(25000);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }

                    System.out.println("Status: Listening for wake word again...");
                }
            }

        } catch (PorcupineException e) {
            System.err.println("Porcupine error: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Microphone error: " + e.getMessage());
        } finally {
            // Cleanup resources
            if (micDataLine != null) {
                micDataLine.stop();
                micDataLine.close();
            }
            if (porcupine != null) {
                porcupine.delete();
            }
        }
    }
}


