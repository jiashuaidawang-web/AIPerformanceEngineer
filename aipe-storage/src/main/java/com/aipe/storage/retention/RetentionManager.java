package com.aipe.storage.retention;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RetentionManager {
    private static final Logger log = LoggerFactory.getLogger(RetentionManager.class);

    private final ScheduledExecutorService scheduler;
    private final int rawDataRetentionDays;
    private final int aggregatedDataRetentionDays;

    public RetentionManager(int rawDataRetentionDays, int aggregatedDataRetentionDays) {
        this.rawDataRetentionDays = rawDataRetentionDays;
        this.aggregatedDataRetentionDays = aggregatedDataRetentionDays;
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "retention-manager");
            t.setDaemon(true);
            return t;
        });
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::cleanExpiredData, 1, 24, TimeUnit.HOURS);
        log.info("RetentionManager started: rawDays={}, aggregatedDays={}", rawDataRetentionDays, aggregatedDataRetentionDays);
    }

    public void cleanExpiredData() {
        log.info("Cleaning expired data: raw > {} days, aggregated > {} days", rawDataRetentionDays, aggregatedDataRetentionDays);
    }

    public void stop() {
        scheduler.shutdown();
        log.info("RetentionManager stopped");
    }
}
