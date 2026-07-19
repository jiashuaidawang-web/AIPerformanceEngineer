package com.aipe.connector.mysql.collector.slowquery;
import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.util.*;
public class SlowQueryCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(SlowQueryCollector.class);
    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String,String> tags = new HashMap<>(); tags.put("source","mysql");
        try {
            ResultSet rs = c.executeQuery("SELECT COUNT(*) as cnt FROM performance_schema.events_statements_summary_by_digest WHERE AVG_TIMER_WAIT > 0 LIMIT 10");
            if (rs.next()) {
                ResultSet slowRs = c.executeQuery("SELECT DIGEST_TEXT, COUNT_STAR, AVG_TIMER_WAIT/1000000000 as avg_ms, SUM_TIMER_WAIT/1000000000 as total_ms FROM performance_schema.events_statements_summary_by_digest WHERE AVG_TIMER_WAIT > 0 ORDER BY AVG_TIMER_WAIT DESC LIMIT 10");
                while (slowRs.next()) {
                    String digest = slowRs.getString("DIGEST_TEXT");
                    if (digest != null && digest.length() > 100) digest = digest.substring(0, 100) + "...";
                    tags.put("query_digest", digest != null ? digest : "unknown");
                    r.add(b(agentId,cid,now,"mysql.slowquery.count_star",slowRs.getDouble("COUNT_STAR"),"count",tags));
                    r.add(b(agentId,cid,now,"mysql.slowquery.avg_ms",slowRs.getDouble("avg_ms"),"ms",tags));
                    r.add(b(agentId,cid,now,"mysql.slowquery.total_ms",slowRs.getDouble("total_ms"),"ms",tags));
                }
                slowRs.close();
            }
            rs.close();
        } catch (Exception e) {
            log.debug("performance_schema slow query not available: {}", e.getMessage());
            try {
                ResultSet fb = c.executeQuery("SHOW STATUS LIKE 'Slow_queries'");
                if (fb.next()) r.add(b(agentId,cid,now,"mysql.slowquery.count",fb.getDouble("Value"),"count",tags));
                fb.close();
            } catch (Exception e2) { log.warn("Fallback failed: {}", e2.getMessage()); }
        }
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "slowquery"; }
}
