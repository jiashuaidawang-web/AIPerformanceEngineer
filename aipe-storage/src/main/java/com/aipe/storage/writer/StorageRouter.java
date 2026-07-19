package com.aipe.storage.writer;

import com.aipe.observation.model.Metric;
import com.aipe.observation.model.Observation;
import com.aipe.observation.model.ResourceReference;
import com.aipe.storage.mysql.MySQLDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StorageRouter {
    private static final Logger log = LoggerFactory.getLogger(StorageRouter.class);

    private final ObservationWriter observationWriter;
    private final MetricWriter metricWriter;

    public StorageRouter(ObservationWriter observationWriter, MetricWriter metricWriter) {
        this.observationWriter = observationWriter;
        this.metricWriter = metricWriter;
    }

    public void route(Observation observation) {
        if (observation == null) return;

        // Route metrics to ClickHouse
        if (observation.getMetrics() != null && !observation.getMetrics().isEmpty()) {
            metricWriter.write(observation.getMetrics());
        }

        // Route observation metadata to MySQL (in MVP: log only)
        log.debug("Routed observation: id={}, resource={}", observation.getId(),
                observation.getResource() != null ? observation.getResource().getResourceId() : "null");
    }
}
