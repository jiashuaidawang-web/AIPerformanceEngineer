package com.aipe.observation.emitter;

import com.aipe.observation.model.Observation;
import com.aipe.observation.model.Observation.ObservationState;
import com.aipe.observation.model.Metric;
import com.aipe.observation.model.ResourceReference;
import com.aipe.observation.queue.ObservationQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class ObservationEmitter {
    private static final Logger log = LoggerFactory.getLogger(ObservationEmitter.class);
    private final ObservationQueue queue;

    public ObservationEmitter(ObservationQueue queue) {
        this.queue = queue;
    }

    public void emit(ResourceReference resource, List<Metric> metrics, String connectorType, String agentId) {
        long now = System.currentTimeMillis();
        Observation observation = Observation.builder()
                .id(UUID.randomUUID().toString())
                .resource(resource)
                .metrics(metrics)
                .eventTime(now)
                .receiveTime(now)
                .connectorType(connectorType)
                .agentId(agentId)
                .state(ObservationState.EMITTED)
                .build();
        queue.offer(observation);
        log.debug("Emitted observation: id={}, resource={}", observation.getId(), resource.getResourceId());
    }
}
