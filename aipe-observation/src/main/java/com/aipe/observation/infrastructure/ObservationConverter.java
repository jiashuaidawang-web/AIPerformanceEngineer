package com.aipe.observation.infrastructure;

import com.aipe.observation.domain.Observation;
import com.aipe.observation.domain.ObservationFactory;
import com.aipe.observation.domain.ObservationId;

import java.util.Map;

/**
 * Observation 转换器
 *
 * <p>Domain ↔ Persistence 对象转换（Gateway Law-001：Repository 返回 Domain）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ObservationConverter {

    private ObservationConverter() {
        // 工具类，禁止实例化
    }

    /**
     * Domain → PO（写入 ClickHouse 前调用）
     *
     * @param observation Observation 聚合根
     * @return ObservationPO
     */
    public static ObservationPO toPO(Observation observation) {
        if (observation == null) {
            return null;
        }
        ObservationPO po = new ObservationPO();
        po.setObservationId(observation.getObservationId() != null ? observation.getObservationId().getValue() : null);
        po.setResourceId(observation.getResourceId() != null ? observation.getResourceId() : "");
        po.setResourceType("UNKNOWN");
        po.setSource(observation.getSource() != null ? observation.getSource().name() : "JVM");
        po.setMetricName(observation.getName() != null ? observation.getName() : "");
        po.setMetricType(observation.getType() != null ? observation.getType().name() : "METRIC");
        po.setMetricValue(observation.getValue() != null ? observation.getValue() : 0.0);
        po.setUnit(observation.getUnit() != null ? observation.getUnit() : "");
        po.setConnectorId(observation.getConnectorId() != null ? observation.getConnectorId() : "");
        po.setLabels(observationJsonLabels(observation));
        po.setPayload(observation.getPayload() != null ? observation.getPayload() : "");
        po.setTimestamp(observation.getTimestamp());
        po.setReceivedAt(System.currentTimeMillis());
        return po;
    }

    /**
     * PO → Domain（从 ClickHouse 读取后调用）
     *
     * @param po ObservationPO
     * @return Observation 聚合根
     */
    public static Observation toDomain(ObservationPO po) {
        if (po == null) {
            return null;
        }

        ObservationId id = po.getObservationId() != null ? ObservationId.of(po.getObservationId()) : null;

        return ObservationFactory.reconstruct(
                id,
                po.getResourceId(),
                parseType(po.getMetricType()),
                parseSource(po.getSource()),
                po.getMetricName(),
                po.getMetricValue(),
                po.getUnit(),
                po.getTimestamp(),
                po.getConnectorId(),
                parseLabels(po.getLabels()),
                po.getPayload()
        );
    }

    // ==================== 枚举解析 ====================

    private static com.aipe.observation.domain.ObservationType parseType(String value) {
        if (value == null) {
            return com.aipe.observation.domain.ObservationType.METRIC;
        }
        try {
            return com.aipe.observation.domain.ObservationType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return com.aipe.observation.domain.ObservationType.METRIC;
        }
    }

    private static com.aipe.observation.domain.ObservationSource parseSource(String value) {
        if (value == null) {
            return com.aipe.observation.domain.ObservationSource.JVM;
        }
        try {
            return com.aipe.observation.domain.ObservationSource.valueOf(value);
        } catch (IllegalArgumentException e) {
            return com.aipe.observation.domain.ObservationSource.JVM;
        }
    }

    /**
     * 为 PO 生成 labels JSON String（这里只取空 —— 如需可保留 key1=value1,key2,value2 形式；由 Converter 保持简单）
     */
    private static String observationJsonLabels(Observation observation) {
        if (observation == null || observation.getLabels() == null || observation.getLabels().isEmpty()) {
            return "{}";
        }
        // 简单序列化为 JSON（避免引入额外依赖；格式稳定）
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, String> entry : observation.getLabels().entrySet()) {
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJson(entry.getKey())).append("\":\"")
                    .append(escapeJson(entry.getValue())).append("\"");
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * 解析 labels JSON 字符串（简化解析：仅支持一层 key=\"value\" 形式）
     */
    private static java.util.Map<String, String> parseLabels(String labels) {
        java.util.Map<String, String> map = new java.util.HashMap<>();
        if (labels == null || labels.trim().isEmpty() || "{}".equals(labels.trim())) {
            return map;
        }
        // 极简 JSON 解析：{\"k\":\"v\",...}
        String body = labels.trim();
        if (body.startsWith("{") && body.endsWith("}")) {
            body = body.substring(1, body.length() - 1).trim();
        }
        if (body.isEmpty()) {
            return map;
        }
        // 按逗号切分（不做完整 JSON 解析，项目中暂不需要复杂标签）
        for (String kv : body.split(",")) {
            int eq = kv.indexOf(":");
            if (eq > 0) {
                String k = kv.substring(0, eq).trim();
                String v = kv.substring(eq + 1).trim();
                k = unwrapJsonString(k);
                v = unwrapJsonString(v);
                if (!k.isEmpty()) {
                    map.put(k, v);
                }
            }
        }
        return map;
    }

    private static String unwrapJsonString(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
