package com.aipe.connector.mysql.collector.bufferpool;
import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.util.*;
public class BufferPoolCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(BufferPoolCollector.class);
    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String,String> tags = new HashMap<>(); tags.put("source","mysql");
        Map<String,Long> vals = new HashMap<>();
        c.query("SHOW STATUS LIKE 'Innodb_buffer_pool%'", rs -> {
            while (rs.next()) {
                try {
                    String name = rs.getString("Variable_name");
                    long val = Long.parseLong(rs.getString("Value"));
                    vals.put(name, val);
                    r.add(b(agentId,cid,now,"mysql.bufferpool."+name.toLowerCase(),(double)val,"count",tags));
                } catch (Exception e) {}
            }
        });
        Long total = vals.get("Innodb_buffer_pool_pages_total");
        Long data = vals.get("Innodb_buffer_pool_pages_data");
        if (total != null && data != null && total > 0) {
            r.add(b(agentId,cid,now,"mysql.bufferpool.usage_percent",(double)data/total*100,"%",tags));
        }
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "bufferpool"; }
}
