package com.aipe.connector.redis.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public final class RedisSupport {
    private static final Logger log = LoggerFactory.getLogger(RedisSupport.class);

    private RedisSupport() {}

    /**
     * Parse Redis INFO string to key-value map
     */
    public static Map<String, String> parseInfo(String info) {
        Map<String, String> map = new HashMap<>();
        if (info == null || info.isEmpty()) return map;
        for (String line : info.split("\r\n")) {
            if (line.startsWith("#") || !line.contains(":")) continue;
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        return map;
    }

    /**
     * Safely parse double from string
     */
    public static double safeParseDouble(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Safely parse long from string
     */
    public static long safeParseLong(String value, long defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
