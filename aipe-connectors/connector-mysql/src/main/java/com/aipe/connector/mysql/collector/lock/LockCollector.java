package com.aipe.connector.mysql.collector.lock;
import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.util.*;
public class LockCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(LockCollector.class);
    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String,String> tags = new HashMap<>(); tags.put("source","mysql");
        try {
            ResultSet rs = c.executeQuery("SELECT COUNT(*) as wait_count FROM performance_schema.data_lock_waits");
            if (rs.next()) r.add(b(agentId,cid,now,"mysql.lock.wait_count",rs.getDouble("wait_count"),"count",tags));
            rs.close();
            ResultSet ir = c.executeQuery("SHOW STATUS LIKE 'Innodb_row_lock_%'");
            while (ir.next()) {
                try { r.add(b(agentId,cid,now,"mysql.lock."+ir.getString("Variable_name").toLowerCase(),Double.parseDouble(ir.getString("Value")),"count",tags)); }
                catch (NumberFormatException e) {}
            }
            ir.close();
            ResultSet lr = c.executeQuery("SELECT COUNT(*) as cnt FROM performance_schema.data_locks");
            if (lr.next()) r.add(b(agentId,cid,now,"mysql.lock.current_locks",lr.getDouble("cnt"),"count",tags));
            lr.close();
        } catch (Exception e) { log.debug("LockCollector perf_schema not available: {}", e.getMessage()); }
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "lock"; }
}
