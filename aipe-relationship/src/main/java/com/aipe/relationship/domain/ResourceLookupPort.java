package com.aipe.relationship.domain;

/**
 * Resource 查找端口（六边形架构）
 *
 * <p>Relationship 模块在新建关系时需要校验 source/target Resource 存在，但为了避免引入 aipe-resource Boot 模块导致双 DataSource 冲突，
 * <p>本模块通过此端口（Port）抽象 Resource 存在性校验，由 Infrastructure 层通过 JdbcTemplate 直读 MySQL resource 表实现。
 *
 * <p>对齐 IM-009 "Repository Interface In Domain, Implementation In Infrastructure"
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface ResourceLookupPort {

    /**
     * 校验 Resource 是否存在（按 resource_id 查询，仅未删除的 Resource 视为存在）
     *
     * @param resourceId Resource ID（字符串，可以是 UUID 或主键值）
     * @return 是否存在
     */
    boolean exists(String resourceId);
}
