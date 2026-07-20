package com.aipe.resource.domain;

import java.util.List;
import java.util.Optional;

/**
 * 资源仓储接口
 *
 * <p>Gateway Law-001：Repository Returns Domain, Never PO
 * <p>Gateway Law-002：One Aggregate, One Repository
 * <p>Gateway Law-003：Repository Interface In Domain, Implementation In Infrastructure
 *
 * <p>接口定义在 Domain 层，实现放在 Infrastructure 层
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface ResourceRepository {

    /**
     * 保存资源（新增）
     *
     * @param resource 资源聚合根（不可为 null）
     * @return 保存后的资源
     */
    Resource save(Resource resource);

    /**
     * 更新资源
     *
     * @param resource 资源聚合根（不可为 null）
     * @return 更新后的资源
     */
    Resource update(Resource resource);

    /**
     * 根据 ID 查找资源
     *
     * @param id 资源 ID
     * @return Optional Resource
     */
    Optional<Resource> findById(ResourceId id);

    /**
     * 根据业务系统查询资源列表
     *
     * @param businessSystem 业务系统名
     * @return 资源列表（不可为 null）
     */
    List<Resource> findByBusinessSystem(String businessSystem);

    /**
     * 根据资源类型查询
     *
     * @param type 资源类型
     * @return 资源列表
     */
    List<Resource> findByType(ResourceType type);

    /**
     * 根据状态查询
     *
     * @param status 资源状态
     * @return 资源列表
     */
    List<Resource> findByStatus(ResourceStatus status);

    /**
     * 根据父资源 ID 查询子资源
     *
     * @param parentId 父资源 ID
     * @return 子资源列表
     */
    List<Resource> findByParentId(ResourceId parentId);

    /**
     * 查询所有资源
     *
     * @return 全部资源列表
     */
    List<Resource> findAll();

    /**
     * 根据 ID 删除资源（逻辑删除）
     *
     * @param id 资源 ID
     * @return 是否删除成功
     */
    boolean deleteById(ResourceId id);

    /**
     * 统计指定业务系统下的资源数量
     *
     * @param businessSystem 业务系统名
     * @return 数量
     */
    long countByBusinessSystem(String businessSystem);
}
