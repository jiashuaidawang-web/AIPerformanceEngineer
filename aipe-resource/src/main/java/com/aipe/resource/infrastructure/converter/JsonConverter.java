package com.aipe.resource.infrastructure.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * JSON 转换工具
 *
 * <p>处理 labels / attributes 字段在 Map ↔ JSON String 之间的转换
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class JsonConverter {

    private static final Logger log = LoggerFactory.getLogger(JsonConverter.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final TypeReference<Map<String, String>> MAP_TYPE = new TypeReference<Map<String, String>>() {
    };

    private JsonConverter() {
        // 工具类，禁止实例化
    }

    /**
     * Map → JSON String
     *
     * @param map Map 对象
     * @return JSON String（空 Map 返回 "{}"）
     */
    public static String mapToJson(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize map to JSON: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * JSON String → Map
     *
     * @param json JSON String
     * @return Map 对象（解析失败返回空 Map）
     */
    public static Map<String, String> jsonToMap(String json) {
        if (json == null || json.trim().isEmpty() || "{}".equals(json)) {
            return new HashMap<>();
        }
        try {
            Map<String, String> result = OBJECT_MAPPER.readValue(json, MAP_TYPE);
            return result != null ? result : new HashMap<>();
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse JSON to map: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    /**
     * 安全获取字符串值
     *
     * @param map Map
     * @param key 键
     * @return 值（不存在返回 null）
     */
    public static String getStringOrDefault(Map<String, String> map, String key) {
        if (map == null) {
            return null;
        }
        return map.get(key);
    }
}
