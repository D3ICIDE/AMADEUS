package com.AMADEUS.phase;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RewardManager {

    // Reference to the shared, thread-safe score container from AMADEUS_PHASE_3
    private final AtomicInteger pointRef;
    // Reference to the JavaFX application instance for safe UI callback
    private final AMADEUS_PHASE_3 uiCallback;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Random RNG = new Random();

    public RewardManager(AMADEUS_PHASE_3 uiCallback, AtomicInteger pointReference) {
        this.uiCallback = uiCallback;
        this.pointRef = pointReference;
    }


    public void startPointIncrementer() {

        int increment = RNG.nextInt(40, 50);

        System.out.println("Reward System Initialized. Increment amount: " + increment);
        System.out.println("Initial Score: " + pointRef.get());

        Runnable task = () -> {

            int newScore = pointRef.addAndGet(increment);
            System.out.println("Score changed to: " + newScore);


            uiCallback.safeUpdateUI(newScore);
        };


        scheduler.scheduleAtFixedRate(task, 5, 30, TimeUnit.SECONDS);
    }



    public void shutdownScheduler() {
        System.out.println("Shutting down reward scheduler...");
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}