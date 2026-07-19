package com.aipe.common.enums;

/**
 * 指标单位枚举
 */
public enum MetricUnit {

    /** 毫秒 */
    MILLISECONDS("ms"),

    /** 秒 */
    SECONDS("s"),

    /** 百分比 */
    PERCENT("%"),

    /** 字节 */
    BYTES("bytes"),

    /** 千字节 */
    KB("KB"),

    /** 兆字节 */
    MB("MB"),

    /** 吉字节 */
    GB("GB"),

    /** 次数 */
    COUNT("count"),

    /** 次/次 */
    OPS("ops"),

    /** 连接数 */
    CONNECTIONS("connections"),

    /** 线程数 */
    THREADS("threads"),

    /** 未知 */
    NONE("");

    private final String symbol;

    MetricUnit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static MetricUnit fromSymbol(String symbol) {
        if (symbol == null || symbol.trim().isEmpty()) {
            return NONE;
        }
        String normalized = symbol.trim().toLowerCase();
        for (MetricUnit u : values()) {
            if (u.symbol.equals(normalized)) {
                return u;
            }
        }
        return NONE;
    }
}
