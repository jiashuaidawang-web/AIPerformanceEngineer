package com.aipe.storage.writer;

import com.aipe.observation.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservationWriter {
    private static final Logger log = LoggerFactory.getLogger(ObservationWriter.class);

    public void write(Observation observation) {
        // MVP: Write to MySQL observation_metadata table
        log.debug("Writing observation: id={}", observation.getId());
    }
}
