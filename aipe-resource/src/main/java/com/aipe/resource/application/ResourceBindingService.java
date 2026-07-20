package com.aipe.resource.application;

import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceId;
import com.aipe.resource.domain.ResourceRepository;
import com.aipe.resource.domain.ResourceStatus;
import com.aipe.resource.domain.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 资源关系绑定服务
 *
 * <p>管理 Resource 之间的关系（parent-child、依赖等）
 *
 * <p>WP011 实现：先用 MySQL parent_resource_id 字段存储关系
 * <p>后续 WP012+ 会联动图数据库（IM-005）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class ResourceBindingService {

    private static final Logger log = LoggerFactory.getLogger(ResourceBindingService.class);

    @Autowired
    private ResourceRepository resourceRepository;

    /**
     * 绑定资源父子关系
     *
     * @param childResourceId  子资源 ID
     * @param parentResourceId 父资源 ID
     * @return 绑定后的子资源
     */
    @Transactional
    public Resource bindParentChild(ResourceId childResourceId, ResourceId parentResourceId) {
        if (childResourceId == null || parentResourceId == null) {
            throw new IllegalArgumentException("Both childResourceId and parentResourceId are required");
        }
        if (childResourceId.equals(parentResourceId)) {
            throw new IllegalArgumentException("Resource cannot be its own parent");
        }
        Resource child = resourceRepository.findById(childResourceId)
                .orElseThrow(() -> new IllegalStateException("Child resource not found: " + childResourceId));
        Resource parent = resourceRepository.findById(parentResourceId)
                .orElseThrow(() -> new IllegalStateException("Parent resource not found: " + parentResourceId));

        // 在 child 的 attributes 中记录 parent（WP011 MySQL 实现）
        child.setAttribute("parent_resource_id", parentResourceId.getValue());
        Resource updated = resourceRepository.update(child);
        log.info("Bound parent-child: child={}, parent={}", childResourceId, parentResourceId);
        return updated;
    }

    /**
     * 查询资源的所有子资源
     *
     * @param parentId 父资源 ID
     * @return 子资源列表
     */
    public List<Resource> queryChildren(ResourceId parentId) {
        return resourceRepository.findByParentId(parentId);
    }

    /**
     * 查询资源的所有关联资源（父 + 子）
     *
     * @param resourceId 资源 ID
     * @return 关联资源列表
     */
    public java.util.Set<Resource> queryRelatedResources(ResourceId resourceId) {
        java.util.Set<Resource> related = new java.util.HashSet<>();
        Resource resource = resourceRepository.findById(resourceId).orElse(null);
        if (resource == null) {
            return related;
        }
        // 查询子资源
        related.addAll(resourceRepository.findByParentId(resourceId));
        // 查询父资源
        if (resource.getParentResourceId() != null) {
            resourceRepository.findById(resource.getParentResourceId()).ifPresent(related::add);
        }
        return related;
    }

    /**
     * 按业务域查询资源列表
     *
     * @param businessSystem 业务系统名
     * @return 资源列表
     */
    public List<Resource> queryByBusinessDomain(String businessSystem) {
        return resourceRepository.findByBusinessSystem(businessSystem);
    }

    /**
     * 解绑父子关系
     *
     * @param childResourceId 子资源 ID
     * @return 更新后的资源
     */
    @Transactional
    public Resource unbindParentChild(ResourceId childResourceId) {
        Resource child = resourceRepository.findById(childResourceId)
                .orElseThrow(() -> new IllegalStateException("Child resource not found: " + childResourceId));
        child.getAttributes().remove("parent_resource_id");
        Resource updated = resourceRepository.update(child);
        log.info("Unbound parent-child: child={}", childResourceId);
        return updated;
    }
}
