/*package com.AMADEUS.phase;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

import java.io.IOException;

/**
 *  This class is used for listening for voice trigger
 * @author Vaibhav
 * @since 2025
 * @version 0.2
 * @deprecated It has been found that CMU SPHINX 4 isn't reliable
 *
 *
 */

/*public class AMADEUS_PHASE_1 implements Runnable  {
    private final AMADEUS_PHASE_3 uiInstance;
    public AMADEUS_PHASE_1(AMADEUS_PHASE_3 uiInstance) {
        this.uiInstance = uiInstance;
    }
    @Override

    public void run() {
        LiveSpeechRecognizer mayushi = null;
        try {
            // Setting Up Configuration
            Configuration Daru = new Configuration();
            Daru.setAcousticModelPath("src/main/resources/model/en-us");

            //Setting Up Dictionary Path
            String DICTIONARY_PATH = AMADEUS_PHASE_1.class.getResource("/4929.dic").toString();
            Daru.setDictionaryPath(DICTIONARY_PATH);

            String LANGUAGE_MODEL_PATH = AMADEUS_PHASE_1.class.getResource("/4929.lm").toString();
            Daru.setLanguageModelPath(LANGUAGE_MODEL_PATH);

            Daru.setSampleRate(16000);


            // Setting Up Language Model Path
             mayushi = new LiveSpeechRecognizer(Daru);
            mayushi.startRecognition(true);

            while (true) {
                String mail;
                SpeechResult Dmail = mayushi.getResult();
                if (Dmail != null && Dmail.getHypothesis().length() > 0) {
                    // Only act if speech was detected
                    mail = Dmail.getHypothesis();



                    System.out.println("Recognized: " + mail );
                    if (mail.equalsIgnoreCase("Arise")) {
                        System.out.println("AMADEUS Activated....");

                        //  Stop the microphone
                        mayushi.stopRecognition();
                        uiInstance.triggerAnalysis();
                        // Pause the current Listener
                        try {
                            //System.out.println("Listener paused for 5 seconds to await AI response...");
                            Thread.sleep(25000); // Wait 25 seconds
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            //  System.err.println("Listener interrupted during pause.");
                        }

                        // 4. Restart recognition and continue the loop
                        mayushi.startRecognition(true);
                        System.out.println("Status: Listening for 'Arise' again.");

                    }

                } else {
                     System.out.println("No speech detected.");

                }


            }


        }
        catch (IOException e) {
            System.err.println("Speech recognition initialization error: " + e.getMessage());
        }

    }



}
*/