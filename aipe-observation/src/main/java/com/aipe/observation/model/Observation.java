package com.aipe.observation.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Observation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private ResourceReference resource;
    private List<Metric> metrics;
    private long eventTime;
    private long receiveTime;
    private String connectorType;
    private String agentId;
    private ObservationState state;

    public enum ObservationState {
        CREATED, EMITTED, QUEUED, PROCESSING, VALIDATED, SERIALIZED, STORED, COMPLETED, FAILED
    }
}
