package com.aipe.connector.jvm.collector;

import com.aipe.common.domain.ObservationData;
import java.util.List;

public interface JvmCollector {
    List<ObservationData> collect(String agentId, String connectorId);
    String getCollectorName();
}
