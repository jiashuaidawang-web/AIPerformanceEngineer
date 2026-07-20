package com.aipe.resource.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * 资源规格校验
 *
 * <p>封装复杂的业务校验规则，供 Validator 和 Domain 层使用
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public final class ResourceSpecification {

    private ResourceSpecification() {
        // 工具类，禁止实例化
    }

    /**
     * 校验资源是否可以创建
     *
     * @param resource 待校验资源
     * @return 校验错误列表（空表示校验通过）
     */
    public static List<String> validateForCreate(Resource resource) {
        List<String> errors = new ArrayList<>();

        if (resource == null) {
            errors.add("Resource cannot be null");
            return errors;
        }

        // 业务系统必填（Law-001：禁止游离资源）
        if (resource.getBusinessSystem() == null || resource.getBusinessSystem().trim().isEmpty()) {
            errors.add("BusinessSystem is required (Law-001: no orphan resource)");
        }

        // 资源类型必填
        if (resource.getResourceType() == null) {
            errors.add("ResourceType is required");
        }

        // 资源名称必填
        if (resource.getResourceName() == null || resource.getResourceName().getValue().trim().isEmpty()) {
            errors.add("ResourceName is required");
        }

        // 初始状态必须是 RUNNING 或 UNKNOWN
        if (resource.getStatus() != ResourceStatus.RUNNING && resource.getStatus() != ResourceStatus.UNKNOWN) {
            errors.add("New resource status must be RUNNING or UNKNOWN");
        }

        return errors;
    }

    /**
     * 校验资源是否可以更新
     *
     * @param resource 待校验资源
     * @return 校验错误列表
     */
    public static List<String> validateForUpdate(Resource resource) {
        List<String> errors = new ArrayList<>();

        if (resource == null) {
            errors.add("Resource cannot be null");
            return errors;
        }

        if (resource.getId() == null) {
            errors.add("ResourceId is required for update");
        }

        if (resource.getVersion() <= 0) {
            errors.add("Version must be positive for optimistic lock");
        }

        return errors;
    }

    /**
     * 校验状态流转是否合法
     *
     * @param current 当前状态
     * @param target  目标状态
     * @return 是否合法
     */
    public static boolean isValidTransition(ResourceStatus current, ResourceStatus target) {
        if (current == null || target == null) {
            return false;
        }
        return current.canTransitionTo(target);
    }

    /**
     * 校验业务系统名称格式
     *
     * @param businessSystem 业务系统名
     * @return 是否合法
     */
    public static boolean isValidBusinessSystem(String businessSystem) {
        return businessSystem != null && !businessSystem.trim().isEmpty() && businessSystem.length() <= 128;
    }
}
