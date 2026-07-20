package com.aipe.resource.domain;

import java.util.Objects;

/**
 * 资源 ID 值对象
 *
 * <p>不可变，全局唯一标识（Domain Law-001：禁止 String/Long id，全部使用 Value Object）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ResourceId {

    private final String value;

    private ResourceId(String value) {
        this.value = value;
    }

    /**
     * 从字符串创建 ResourceId
     *
     * @param value ID 值（不可为 null 或空）
     * @return ResourceId
     */
    public static ResourceId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ResourceId cannot be null or empty");
        }
        return new ResourceId(value.trim());
    }

    /**
     * 生成新的随机 ResourceId（UUID）
     *
     * @return 新的 ResourceId
     */
    public static ResourceId generate() {
        return new ResourceId(java.util.UUID.randomUUID().toString());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceId that = (ResourceId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
