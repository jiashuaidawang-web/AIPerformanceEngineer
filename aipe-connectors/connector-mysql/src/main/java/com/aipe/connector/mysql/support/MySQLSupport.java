package com.aipe.connector.mysql.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MySQLSupport {
    private static final Logger log = LoggerFactory.getLogger(MySQLSupport.class);

    private MySQLSupport() {}

    public static double safeParseDouble(String value, double defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try { return Double.parseDouble(value.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }

    public static long safeParseLong(String value, long defaultValue) {
        if (value == null || value.trim().isEmpty()) return defaultValue;
        try { return Long.parseLong(value.trim()); }
        catch (NumberFormatException e) { return defaultValue; }
    }
}
