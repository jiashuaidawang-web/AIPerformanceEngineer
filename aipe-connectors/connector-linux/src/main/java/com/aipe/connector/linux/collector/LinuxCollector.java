package com.aipe.connector.linux.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.linux.parser.ProcFileReader;

import java.util.List;

/**
 * Linux 采集器接口
 */
public interface LinuxCollector {
    List<ObservationData> collect(ProcFileReader reader, String agentId, String connectorId);
    String getCollectorName();
}
