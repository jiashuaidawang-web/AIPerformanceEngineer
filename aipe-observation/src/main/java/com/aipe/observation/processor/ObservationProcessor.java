package com.aipe.observation.processor;

import com.aipe.observation.model.Observation;
import com.aipe.observation.model.Observation.ObservationState;
import com.aipe.observation.serializer.ObservationSerializer;
import com.aipe.observation.storage.ObservationStorageWriter;
import com.aipe.observation.validator.ObservationValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ObservationProcessor {
    private static final Logger log = LoggerFactory.getLogger(ObservationProcessor.class);

    private final ObservationValidator validator;
    private final ObservationSerializer serializer;
    private final ObservationStorageWriter storageWriter;
    private final ObservationBatcher batcher;

    public ObservationProcessor(ObservationValidator validator,
                                 ObservationSerializer serializer,
                                 ObservationStorageWriter storageWriter,
                                 ObservationBatcher batcher) {
        this.validator = validator;
        this.serializer = serializer;
        this.storageWriter = storageWriter;
        this.batcher = batcher;
    }

    /**
     * Factory method to create processor with storage callback
     */
    public static ObservationProcessor createWithStorage(Consumer<Observation> storageCallback) {
        ObservationValidator validator = new ObservationValidator();
        ObservationSerializer serializer = new ObservationSerializer();
        ObservationStorageWriter writer = new ObservationStorageWriter(storageCallback);
        ObservationBatcher batcher = new ObservationBatcher(100);
        return new ObservationProcessor(validator, serializer, writer, batcher);
    }

    public void process(Observation observation) {
        try {
            // Step 1: Validate
            observation.setState(ObservationState.PROCESSING);
            ObservationValidator.ValidationResult validationResult = validator.validate(observation);
            if (!validationResult.isValid()) {
                observation.setState(ObservationState.FAILED);
                log.warn("Observation validation failed: id={}, reason={}", observation.getId(), validationResult.getReason());
                return;
            }
            observation.setState(ObservationState.VALIDATED);

            // Step 2: Add to batch
            batcher.add(observation);

            // Step 3: Check if batch is ready
            if (batcher.isReady()) {
                List<Observation> batch = batcher.drain();
                List<String> serialized = new ArrayList<>();
                for (Observation obs : batch) {
                    String json = serializer.serialize(obs);
                    serialized.add(json);
                    obs.setState(ObservationState.SERIALIZED);
                }
                storageWriter.write(serialized);
                for (Observation obs : batch) {
                    obs.setState(ObservationState.STORED);
                }
            }

        } catch (Exception e) {
            observation.setState(ObservationState.FAILED);
            log.error("Error processing observation: id={}", observation.getId(), e);
        }
    }
}
