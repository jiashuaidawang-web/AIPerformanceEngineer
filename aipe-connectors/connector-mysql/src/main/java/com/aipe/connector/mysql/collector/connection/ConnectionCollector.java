package com.aipe.connector.mysql.collector.connection;
import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.util.*;
public class ConnectionCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(ConnectionCollector.class);
    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String,String> tags = new HashMap<>(); tags.put("source","mysql");
        try {
            ResultSet rs = c.executeQuery("SHOW STATUS LIKE 'Threads_%'");
            while (rs.next()) {
                try { r.add(b(agentId,cid,now,"mysql.connection."+rs.getString("Variable_name").toLowerCase(),Double.parseDouble(rs.getString("Value")),"count",tags)); }
                catch (NumberFormatException e) {}
            }
            rs.close();
            ResultSet mr = c.executeQuery("SHOW VARIABLES LIKE 'max_connections'");
            if (mr.next()) { try { r.add(b(agentId,cid,now,"mysql.connection.max_connections",Double.parseDouble(mr.getString("Value")),"count",tags)); } catch (NumberFormatException e) {} }
            mr.close();
        } catch (Exception e) { log.error("connection failed", e); }
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "connection"; }
}
