package com.aipe.timeline.domain;

/**
 * Timeline 规格校验
 *
 * <p>封装 TimelineQuery 的校验规则
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class TimelineSpecification {

    private static final long MAX_TIMELINE_RANGE_MS = 7L * 24 * 3600 * 1000; // 最多 7 天
    private static final int MAX_POINTS = 100_000; // 最大采样点数

    private TimelineSpecification() {
        // 工具类，禁止实例化
    }

    /**
     * 校验 TimelineQuery 是否合法
     *
     * @param query 待校验
     * @throws IllegalArgumentException 校验失败
     */
    public static void validate(TimelineQuery query) {
        if (query == null) {
            throw new IllegalArgumentException("TimelineQuery cannot be null");
        }
        if (query.getResourceId() == null || query.getResourceId().trim().isEmpty()) {
            throw new IllegalArgumentException("ResourceId is required (Law-002)");
        }
        if (query.getStartTime() <= 0 || query.getEndTime() <= 0) {
            throw new IllegalArgumentException("Start time and end time must be positive");
        }
        if (query.getStartTime() >= query.getEndTime()) {
            throw new IllegalArgumentException("Start time must be less than end time");
        }
        if (query.getEndTime() - query.getStartTime() > MAX_TIMELINE_RANGE_MS) {
            throw new IllegalArgumentException(
                    "Timeline range cannot exceed 7 days (got "
                            + (query.getEndTime() - query.getStartTime()) / 3600_000 + " hours)");
        }
        if (query.getLimit() > MAX_POINTS) {
            throw new IllegalArgumentException("Limit cannot exceed " + MAX_POINTS);
        }
    }

    /**
     * 检测并自动换算时间戳单位（毫秒级 or 秒级）
     *
     * <p>ClickHouse timestamp 字段为 DateTime64(3)（毫秒）；
     * 当输入时间戳 <= 1e12 时自动视为秒级，换算为毫秒
     *
     * @param timestamp 时间戳（秒 or 毫秒）
     * @return 毫秒级时间戳
     */
    public static long normalizeTimestamp(long timestamp) {
        // 2100-01-01 毫秒值 ~ 4.1e12；若低于此视为秒级
        if (timestamp > 0 && timestamp < 1_000_000_000_000L) {
            return timestamp * 1000L;
        }
        return timestamp;
    }
}
