package com.aipe.connector.mysql.collector.transaction;
import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.ResultSet;
import java.util.*;
public class TransactionCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(TransactionCollector.class);
    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        if (c == null) { log.debug("MySQLConnection is null, skipping"); return new ArrayList<>(); }
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String,String> tags = new HashMap<>(); tags.put("source","mysql");
        c.query("SHOW STATUS LIKE 'Innodb_trx%_%'", rs -> {
            while (rs.next()) {
                try { r.add(b(agentId,cid,now,"mysql.transaction."+rs.getString("Variable_name").toLowerCase(),Double.parseDouble(rs.getString("Value")),"count",tags)); }
                catch (Exception e) {}
            }
        });
        return r;
    }
    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String,String> tags) {
        return ObservationData.builder().agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node").collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }
    @Override public String getCollectorName() { return "transaction"; }
}
