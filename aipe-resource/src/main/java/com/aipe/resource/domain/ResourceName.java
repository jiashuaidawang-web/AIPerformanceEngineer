package com.aipe.resource.domain;

import java.util.Objects;

/**
 * 资源名称值对象
 *
 * <p>不可变，同类型下唯一
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ResourceName {

    private static final int MAX_LENGTH = 256;

    private final String value;

    private ResourceName(String value) {
        this.value = value;
    }

    /**
     * 从字符串创建 ResourceName
     *
     * @param value 名称（不可为 null 或空，长度不超过 256）
     * @return ResourceName
     */
    public static ResourceName of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ResourceName cannot be null or empty");
        }
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("ResourceName length cannot exceed " + MAX_LENGTH);
        }
        return new ResourceName(value.trim());
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceName that = (ResourceName) o;
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
