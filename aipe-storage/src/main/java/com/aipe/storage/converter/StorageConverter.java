package com.aipe.storage.converter;

import com.aipe.observation.model.Metric;
import com.aipe.observation.model.Observation;

public class StorageConverter {
    
    public String toMetricInsertSQL(Observation observation) {
        if (observation == null || observation.getMetrics() == null) return "";
        StringBuilder sb = new StringBuilder();
        for (Metric m : observation.getMetrics()) {
            sb.append(String.format(
                "INSERT INTO metric_observation (timestamp, resource_id, metric_name, metric_value, labels) VALUES (%d, '%s', '%s', %f, '%s');",
                observation.getEventTime(),
                observation.getResource() != null ? observation.getResource().getResourceId() : "",
                m.getName(), m.getValue(), m.getTags()
            ));
        }
        return sb.toString();
    }
}
