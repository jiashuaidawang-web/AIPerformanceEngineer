package com.aipe.connector.mysql.collector.transaction;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.mysql.client.MySQLConnection;
import com.aipe.connector.mysql.collector.MySQLCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionCollector implements MySQLCollector {
    private static final Logger log = LoggerFactory.getLogger(TransactionCollector.class);

    @Override
    public List<ObservationData> collect(MySQLConnection c, String agentId, String cid) {
        List<ObservationData> r = new ArrayList<>();
        long now = System.currentTimeMillis();
        Map<String, String> tags = new HashMap<>();
        tags.put("source", "mysql");
        try {
            ResultSet rs = c.executeQuery("SHOW STATUS LIKE 'Innodb_trx%_%'");
            while (rs.next()) {
                try { r.add(b(agentId, cid, now, "mysql.transaction." + rs.getString("Variable_name").toLowerCase(), Double.parseDouble(rs.getString("Value")), "count", tags)); }
                catch (NumberFormatException e) {}
            }
            rs.close();
            ResultSet hr = c.executeQuery("SHOW ENGINE INNODB STATUS");
            if (hr.next()) {
                String status = hr.getString("Status");
                if (status != null) {
                    int idx = status.indexOf("History list length");
                    if (idx >= 0) {
                        String sub = status.substring(idx + "History list length".length()).trim();
                        String num = sub.split("\\s+")[0];
                        try { r.add(b(agentId, cid, now, "mysql.transaction.history_list_length", Double.parseDouble(num), "count", tags)); }
                        catch (NumberFormatException e) {}
                    }
                }
            }
            hr.close();
        } catch (Exception e) { log.debug("transaction collector failed: {}", e.getMessage()); }
        return r;
    }

    private ObservationData b(String a, String c, long t, String n, double v, String u, Map<String, String> tags) {
        return ObservationData.builder()
                .agentId(a).connectorId(c).connectorType("MYSQL").targetResource("mysql-node")
                .collectTime(t).metricName(n).metricValue(v).unit(u).tags(new HashMap<>(tags)).build();
    }

    @Override public String getCollectorName() { return "transaction"; }
}
