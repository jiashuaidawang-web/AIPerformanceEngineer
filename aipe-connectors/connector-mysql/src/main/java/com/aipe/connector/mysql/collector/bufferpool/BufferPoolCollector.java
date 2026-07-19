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
        try {
            ResultSet rs = c.executeQuery("SHOW STATUS LIKE 'Innodb_buffer_pool%'");
            Long pagesTotal = null, pagesData = null;
            while (rs.next()) {
                String name = rs.getString("Variable_name");
                String value = rs.getString("Value");
                try {
                    double val = Double.parseDouble(value);
                    r.add(b(agentId,cid,now,"mysql.bufferpool."+name.toLowerCase(),val,"count",tags));
                    if ("Innodb_buffer_pool_pages_total".equals(name)) pagesTotal = (long)val;
                    if ("Innodb_buffer_pool_pages_data".equals(name)) pagesData = (long)val;
                } catch (NumberFormatException e) {}
            }
            rs.close();
            if (pagesTotal != null && pagesData != null && pagesTotal > 0) {
                r.add(b(agentId,cid,now,"mysql.bufferpool.usage_percent",(double)pagesData/pagesTotal*100,"%",tags));
            }
        } catch (Exception e) { log.error("bufferpool failed", e); }
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "bufferpool"; }
}
