package com.aipe.storage.repository;

import com.aipe.observation.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ObservationRepository {
    private static final Logger log = LoggerFactory.getLogger(ObservationRepository.class);

    public void save(Observation observation) {
        log.debug("Saving observation: id={}", observation.getId());
    }

    public List<Observation> findByResourceId(String resourceId) {
        log.debug("Querying observations by resourceId={}", resourceId);
        return java.util.Collections.emptyList();
    }
}
