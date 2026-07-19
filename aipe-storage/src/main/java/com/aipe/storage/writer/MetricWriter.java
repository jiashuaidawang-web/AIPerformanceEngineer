package com.aipe.storage.writer;

import com.aipe.observation.model.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MetricWriter {
    private static final Logger log = LoggerFactory.getLogger(MetricWriter.class);

    public void write(List<Metric> metrics) {
        // MVP: Batch write to ClickHouse metric_observation table
        log.debug("Writing {} metrics to ClickHouse", metrics.size());
    }
}
