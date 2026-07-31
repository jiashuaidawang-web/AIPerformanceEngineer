package com.aipe.connector.zookeeper.collector;

import com.aipe.common.domain.ObservationData;
import com.aipe.common.enums.MetricUnit;
import com.aipe.connector.zookeeper.config.ZooKeeperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

/**
 * ZooKeeper 四字命令采集器
 *
 * <p>通过 ZooKeeper 四字命令协议采集指标:
 * <ul>
 *   <li>mntr - 监控指标 (节点数、Watch 数、连接数、延迟等)</li>
 *   <li>stat - 状态信息 (版本、模式、连接数)</li>
 *   <li>cons - 连接详情</li>
 *   <li>ruok - 健康检查</li>
 * </ul>
 *
 * <p>使用 TCP 连接发送四字命令，解析 key=value 格式响应
 */
public class FourLetterCommandCollector implements ZooKeeperCollector {

    private static final Logger log = LoggerFactory.getLogger(FourLetterCommandCollector.class);

    private static final String[] COMMANDS = {"mntr", "stat", "ruok"};

    @Override
    public List<ObservationData> collect(ZooKeeperConfig config, String agentId, String connectorId) {
        List<ObservationData> results = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (String command : COMMANDS) {
            try {
                String response = sendFourLetterCommand(config.getHost(), config.getPort(), command);
                if (response != null && !response.isEmpty()) {
                    parseResponse(response, command, results, agentId, connectorId, now);
                }
            } catch (Exception e) {
                log.warn("Failed to execute ZK command '{}': {}", command, e.getMessage());
            }
        }

        log.debug("Collected {} ZooKeeper metrics", results.size());
        return results;
    }

    /**
     * 发送四字命令并获取响应
     */
    private String sendFourLetterCommand(String host, int port, String command) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            socket.setSoTimeout(5000);
            out.println(command);
            out.flush();

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();

        } catch (Exception e) {
            log.debug("ZK command '{}' failed: {}", command, e.getMessage());
            return null;
        }
    }

    /**
     * 解析 key=value 格式响应
     */
    private void parseResponse(String response, String command, List<ObservationData> results,
                                String agentId, String connectorId, long now) {
        String[] lines = response.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("zk_") == false && command.equals("mntr")) {
                continue;
            }

            String[] parts = line.split("\t|=|\\s+", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();

                try {
                    double numericValue = Double.parseDouble(value);
                    Map<String, String> tags = new HashMap<>();
                    tags.put("source", "4letter");
                    tags.put("command", command);

                    results.add(ObservationData.builder()
                            .agentId(agentId).connectorId(connectorId).connectorType("ZOOKEEPER")
                            .targetResource("zk-" + command)
                            .collectTime(now).metricName("zookeeper." + key)
                            .metricValue(numericValue)
                            .unit(getUnit(key)).tags(tags).build());
                } catch (NumberFormatException e) {
                    // 非数值指标跳过 (如 version, mode 等)
                }
            }
        }
    }

    private String getUnit(String key) {
        if (key.contains("latency")) return "ms";
        if (key.contains("bytes")) return "bytes";
        if (key.contains("count") || key.contains("num") || key.contains("connections")) return "count";
        return "";
    }

    @Override
    public String getCollectorName() {
        return "4letter-command";
    }
}
