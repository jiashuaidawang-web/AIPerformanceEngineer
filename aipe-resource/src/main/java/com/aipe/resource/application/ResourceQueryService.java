package com.aipe.resource.application;

import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceId;
import com.aipe.resource.domain.ResourceStatus;
import com.aipe.resource.domain.ResourceType;
import com.aipe.resource.domain.ResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 资源查询服务
 *
 * <p>负责各种查询场景（Orchestration Law-002：Query Service）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Service
public class ResourceQueryService {

    private static final Logger log = LoggerFactory.getLogger(ResourceQueryService.class);

    @Autowired
    private ResourceRepository resourceRepository;

    /**
     * 根据 ID 查询
     *
     * @param resourceId 资源 ID
     * @return Resource（可能为 null）
     */
    public Resource findById(ResourceId resourceId) {
        return resourceRepository.findById(resourceId).orElse(null);
    }

    /**
     * 根据 ID 查询（字符串版本）
     *
     * @param resourceId 资源 ID 字符串
     * @return Resource（可能为 null）
     */
    public Resource findById(String resourceId) {
        if (resourceId == null) {
            return null;
        }
        return resourceRepository.findById(ResourceId.of(resourceId)).orElse(null);
    }

    /**
     * 查询所有资源
     *
     * @return 全部资源列表
     */
    public List<Resource> findAll() {
        return resourceRepository.findAll();
    }

    /**
     * 按业务系统查询
     *
     * @param businessSystem 业务系统名
     * @return 资源列表
     */
    public List<Resource> findByBusinessSystem(String businessSystem) {
        return resourceRepository.findByBusinessSystem(businessSystem);
    }

    /**
     * 按类型查询
     *
     * @param type 资源类型
     * @return 资源列表
     */
    public List<Resource> findByType(ResourceType type) {
        return resourceRepository.findByType(type);
    }

    /**
     * 按状态查询
     *
     * @param status 资源状态
     * @return 资源列表
     */
    public List<Resource> findByStatus(ResourceStatus status) {
        return resourceRepository.findByStatus(status);
    }

    /**
     * 按父资源 ID 查询子资源
     *
     * @param parentId 父资源 ID
     * @return 子资源列表
     */
    public List<Resource> findByParentId(ResourceId parentId) {
        return resourceRepository.findByParentId(parentId);
    }

    /**
     * 统计业务系统下的资源数量
     *
     * @param businessSystem 业务系统名
     * @return 数量
     */
    public long countByBusinessSystem(String businessSystem) {
        return resourceRepository.countByBusinessSystem(businessSystem);
    }

    /**
     * 检查资源是否存在
     *
     * @param resourceId 资源 ID
     * @return 是否存在
     */
    public boolean exists(ResourceId resourceId) {
        return resourceRepository.findById(resourceId).isPresent();
    }
}
