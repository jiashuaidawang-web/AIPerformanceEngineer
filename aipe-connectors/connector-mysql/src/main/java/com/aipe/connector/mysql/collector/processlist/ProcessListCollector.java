package com.aipe.connector.mysql.collector.processlist;
import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.util.*;
public class ProcessListCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(ProcessListCollector.class);
    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        if (c == null) { log.debug("MySQLConnection is null, skipping"); return new ArrayList<>(); }
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String,String> tags = new HashMap<>(); tags.put("source","mysql");
        c.query("SHOW FULL PROCESSLIST", rs -> {
            int total = 0; Map<String,Integer> states = new HashMap<>();
            while (rs.next()) {
                total++;
                String st = rs.getString("State");
                if (st == null || st.isEmpty()) st = "NULL";
                states.merge(st, 1, Integer::sum);
            }
            r.add(b(agentId,cid,now,"mysql.processlist.total",(double)total,"count",tags));
            for (Map.Entry<String,Integer> e : states.entrySet()) {
                tags.put("state", e.getKey());
                r.add(b(agentId,cid,now,"mysql.processlist.state."+e.getKey().toLowerCase(),(double)e.getValue(),"count",tags));
            }
        });
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "processlist"; }
}
