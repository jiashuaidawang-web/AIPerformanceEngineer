package com.aipe.observation.serializer;

import com.aipe.observation.model.Observation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObservationSerializer {
    private static final Logger log = LoggerFactory.getLogger(ObservationSerializer.class);
    private final ObjectMapper objectMapper;

    public ObservationSerializer() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public String serialize(Observation observation) {
        try {
            return objectMapper.writeValueAsString(observation);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize observation: id={}", observation.getId(), e);
            return "{}";
        }
    }
}
