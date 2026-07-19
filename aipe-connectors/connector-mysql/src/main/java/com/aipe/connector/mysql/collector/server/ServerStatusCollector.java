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
        try {
            ResultSet rs = c.executeQuery("SHOW GLOBAL STATUS");
            Map<String,String> m = new HashMap<>();
            while (rs.next()) m.put(rs.getString("Variable_name"), rs.getString("Value"));
            rs.close();
            String up = m.get("Uptime");
            if (up != null) r.add(b(agentId,cid,now,"mysql.server.uptime",Double.parseDouble(up),"s",tags));
            String q = m.get("Questions");
            if (up != null && q != null) r.add(b(agentId,cid,now,"mysql.server.qps",Double.parseDouble(q)/Math.max(1,Double.parseDouble(up)),"ops",tags));
            for (String k : KEYS) {
                String v = m.get(k);
                if (v != null) { try { r.add(b(agentId,cid,now,"mysql.status."+k.toLowerCase(),Double.parseDouble(v),"count",tags)); } catch (NumberFormatException e) {} }
            }
        } catch (Exception e) { log.error("server-status failed", e); }
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "server-status"; }
}
