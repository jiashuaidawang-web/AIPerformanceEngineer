package com.aipe.connector.mysql.collector.server;
import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.util.*;
public class ServerStatusCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(ServerStatusCollector.class);
    private static final String[] KEYS = {"Threads_connected","Threads_running","Questions","Com_select","Com_insert","Com_update","Com_delete","Innodb_row_lock_waits","Innodb_row_lock_time","Slow_queries","Uptime"};
    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String,String> tags = new HashMap<>(); tags.put("source","mysql");
        Map<String,String> m = new HashMap<>();
        c.query("SHOW GLOBAL STATUS", rs -> {
            while (rs.next()) m.put(rs.getString("Variable_name"), rs.getString("Value"));
        });
        if (m.isEmpty()) { log.debug("ServerStatus: no data"); return r; }
        String up = m.get("Uptime");
        if (up != null) r.add(b(agentId,cid,now,"mysql.server.uptime",parseDouble(up),"s",tags));
        String q = m.get("Questions");
        if (up != null && q != null) { try { r.add(b(agentId,cid,now,"mysql.server.qps",parseDouble(q)/Math.max(1,parseDouble(up)),"ops",tags)); } catch (Exception e) {} }
        for (String k : KEYS) {
            String v = m.get(k);
            if (v != null) { try { r.add(b(agentId,cid,now,"mysql.status."+k.toLowerCase(),parseDouble(v),"count",tags)); } catch (Exception e) {} }
        }
        return r;
    }
    private double parseDouble(String s) { try { return Double.parseDouble(s); } catch (Exception e) { return 0; } }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "server-status"; }
}
