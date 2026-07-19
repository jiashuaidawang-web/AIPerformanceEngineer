package com.aipe.connector.mysql.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import java.util.List;

public interface MySQLCollector {
    List<ObservationData> collect(MySQLConnection connection, String agentId, String connectorId);
    String getCollectorName();
}
