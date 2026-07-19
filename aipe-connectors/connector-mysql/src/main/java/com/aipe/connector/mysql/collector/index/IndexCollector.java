package com.aipe.connector.mysql.collector.index;
import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.util.*;
public class IndexCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(IndexCollector.class);
    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String,String> tags = new HashMap<>(); tags.put("source","mysql");
        c.query("SELECT COUNT(*) as total FROM information_schema.STATISTICS", rs -> {
            while (rs.next()) {
                try { r.add(b(agentId,cid,now,"mysql.index.total",rs.getDouble("total"),"count",tags)); }
                catch (Exception e) {}
            }
        });
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "index"; }
}
