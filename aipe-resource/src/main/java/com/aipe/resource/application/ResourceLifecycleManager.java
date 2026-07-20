package com.aipe.resource.application;

import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceId;
import com.aipe.resource.domain.ResourceName;
import com.aipe.resource.domain.ResourceStatus;
import com.aipe.resource.domain.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 资源生命周期管理器
 *
 * <p>Law-001：Resource 是所有资源相关的唯一入口
 * <p>Orchestration Law-001：Application Is The Only Transaction Owner
 *
 * <p>所有资源操作（创建/更新/删除/状态流转）必须通过本类，禁止直接操作 ResourceRepository
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class ResourceLifecycleManager {

    private static final Logger log = LoggerFactory.getLogger(ResourceLifecycleManager.class);

    @Autowired
    private ResourceRepository resourceRepository;

    @Autowired
    private ResourceValidator resourceValidator;

    /**
     * 创建资源（自动分配 ID、初始化状态、校验）
     *
     * @param resource 待创建资源（ID 可为 null，会自动生成）
     * @return 创建后的资源（含生成的 ID）
     */
    @Transactional
    public Resource createResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        // 校验
        resourceValidator.validateCreate(resource);
        // 保存
        Resource saved = resourceRepository.save(resource);
        log.info("Resource created: id={}, name={}, type={}, system={}",
                saved.getId(), saved.getResourceName(), saved.getResourceType(), saved.getBusinessSystem());
        return saved;
    }

    /**
     * 更新资源基础信息
     *
     * @param resource 待更新资源（必须含 ID）
     * @return 更新后的资源
     */
    @Transactional
    public Resource updateResource(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        resourceValidator.validateUpdate(resource);
        // 确认资源存在
        Resource existing = resourceRepository.findById(resource.getId())
                .orElseThrow(() -> new IllegalStateException("Resource not found: " + resource.getId()));
        Resource updated = resourceRepository.update(resource);
        log.info("Resource updated: id={}, version={}", updated.getId(), updated.getVersion());
        return updated;
    }

    /**
     * 更新资源状态（带合法性校验）
     *
     * @param resourceId 资源 ID
     * @param newStatus  目标状态
     * @return 更新后的资源
     */
    @Transactional
    public Resource updateResourceStatus(ResourceId resourceId, ResourceStatus newStatus) {
        if (resourceId == null) {
            throw new IllegalArgumentException("ResourceId cannot be null");
        }
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalStateException("Resource not found: " + resourceId));
        // 校验流转合法性
        resourceValidator.validateStatusTransition(resource, newStatus);
        // 执行流转
        resource.transitionStatus(newStatus);
        Resource updated = resourceRepository.update(resource);
        log.info("Resource status updated: id={}, newStatus={}", resourceId, newStatus);
        return updated;
    }

    /**
     * 删除资源（逻辑删除）
     *
     * @param resourceId 资源 ID
     * @return 是否删除成功
     */
    @Transactional
    public boolean deleteResource(ResourceId resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("ResourceId cannot be null");
        }
        boolean deleted = resourceRepository.deleteById(resourceId);
        if (deleted) {
            log.info("Resource deleted: id={}", resourceId);
        }
        return deleted;
    }

    /**
     * 绑定业务归属域（更新 businessSystem）
     *
     * @param resourceId     资源 ID
     * @param businessSystem 业务系统名
     * @return 更新后的资源
     */
    @Transactional
    public Resource bindBusinessDomain(ResourceId resourceId, String businessSystem) {
        if (resourceId == null) {
            throw new IllegalArgumentException("ResourceId cannot be null");
        }
        resourceValidator.validateBusinessSystem(businessSystem);
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalStateException("Resource not found: " + resourceId));
        // 更新 businessSystem
        resource.getAttributes().put("business_system", businessSystem);
        Resource updated = resourceRepository.update(resource);
        log.info("Resource business domain bound: id={}, system={}", resourceId, businessSystem);
        return updated;
    }
}
