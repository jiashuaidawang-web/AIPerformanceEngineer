package com.aipe.connector.mysql.collector.index;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IndexCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(IndexCollector.class);

    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "mysql");
        try {
            ResultSet rs = c.executeQuery("SELECT COUNT(*) as unused_count FROM performance_schema.table_io_waits_summary_by_index_usage WHERE INDEX_NAME IS NOT NULL AND COUNT_STAR = 0");
            if (rs.next()) r.add(b(agentId, cid, now, "mysql.index.unused_count", rs.getDouble("unused_count"), "count", tags));
            rs.close();
            ResultSet tr = c.executeQuery("SELECT COUNT(*) as total FROM information_schema.STATISTICS");
            if (tr.next()) r.add(b(agentId, cid, now, "mysql.index.total", tr.getDouble("total"), "count", tags));
            tr.close();
        } catch (Exception e) { log.debug("index collector failed: {}", e.getMessage()); }
        return r;
    }

    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String, String> tags) {
        return ObservationData.builder()
                .agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node")
                .collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }

    @Override public String getCollectorName() { return "index"; }
}
