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
        if (c == null) { log.debug("MySQLConnection is null, skipping"); return new ArrayList<>(); }
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String,String> tags = new HashMap<>(); tags.put("source","mysql");
        c.query("SHOW STATUS LIKE 'Innodb_row_lock_%'", rs -> {
            while (rs.next()) {
                try { r.add(b(agentId,cid,now,"mysql.lock."+rs.getString("Variable_name").toLowerCase(),Double.parseDouble(rs.getString("Value")),"count",tags)); }
                catch (Exception e) {}
            }
        });
        // MySQL 8.0+ 用 INNODB_TRX 替代了 INNODB_LOCKS
        c.query("SELECT COUNT(*) as cnt FROM information_schema.INNODB_TRX", rs -> {
            while (rs.next()) {
                try { r.add(b(agentId,cid,now,"mysql.lock.current",rs.getDouble("cnt"),"count",tags)); }
                catch (Exception e) {}
            }
        });
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "lock"; }
}
