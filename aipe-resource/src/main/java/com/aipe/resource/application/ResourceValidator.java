package com.aipe.resource.application;

import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceSpecification;
import com.aipe.resource.domain.ResourceStatus;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 资源校验器
 *
 * <p>封装复杂的校验逻辑，供 Application 层调用
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ResourceValidator {

    /**
     * 校验资源是否可以创建
     *
     * @param resource 待校验资源
     * @throws IllegalArgumentException 校验失败
     */
    public void validateCreate(Resource resource) {
        List<String> errors = ResourceSpecification.validateForCreate(resource);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Resource validation failed: " + String.join("; ", errors));
        }
    }

    /**
     * 校验资源是否可以更新
     *
     * @param resource 待校验资源
     * @throws IllegalArgumentException 校验失败
     */
    public void validateUpdate(Resource resource) {
        List<String> errors = ResourceSpecification.validateForUpdate(resource);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Resource update validation failed: " + String.join("; ", errors));
        }
    }

    /**
     * 校验状态流转是否合法
     *
     * @param resource  资源
     * @param newStatus 目标状态
     * @throws IllegalStateException 非法流转
     */
    public void validateStatusTransition(Resource resource, ResourceStatus newStatus) {
        if (!ResourceSpecification.isValidTransition(resource.getStatus(), newStatus)) {
            throw new IllegalStateException(
                    String.format("Cannot transition status from %s to %s for resource %s",
                            resource.getStatus(), newStatus, resource.getId()));
        }
    }

    /**
     * 校验业务系统名是否合法
     *
     * @param businessSystem 业务系统名
     * @throws IllegalArgumentException 校验失败
     */
    public void validateBusinessSystem(String businessSystem) {
        if (!ResourceSpecification.isValidBusinessSystem(businessSystem)) {
            throw new IllegalArgumentException("Invalid businessSystem: " + businessSystem);
        }
    }
}
