package com.aipe.observation.storage;

import com.aipe.observation.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Consumer;

/**
 * Observation Storage Writer
 *
 * <p>Bridges Observation Pipeline with Storage Layer.
 * Uses a Consumer callback to avoid circular dependency between aipe-observation and aipe-storage.
 */
public class ObservationStorageWriter {
    private static final Logger log = LoggerFactory.getLogger(ObservationStorageWriter.class);

    private final Consumer<Observation> storageCallback;

    public ObservationStorageWriter(Consumer<Observation> storageCallback) {
        this.storageCallback = storageCallback;
    }

    public void write(List<String> serializedObservations) {
        // MVP: Log serialized observations (later: parse JSON and route to storage)
        for (String json : serializedObservations) {
            log.debug("Storing observation: {}", json);
        }
        log.info("Batch stored {} observations", serializedObservations.size());
    }

    /**
     * Write directly from Observation object (used when Pipeline is connected to Storage)
     */
    public void writeObservation(Observation observation) {
        if (storageCallback != null) {
            storageCallback.accept(observation);
        }
    }
}
