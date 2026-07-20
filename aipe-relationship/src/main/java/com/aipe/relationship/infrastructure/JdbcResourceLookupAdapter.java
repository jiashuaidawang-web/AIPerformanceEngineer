package com.aipe.relationship.infrastructure;

import com.aipe.relationship.domain.ResourceLookupPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Resource 查找适配器（通过 JdbcTemplate 直读 MySQL resource 表）
 *
 * <p>对齐 WP013 Blueprint §9.1: Relationship 新建时校验 source/target Resource 存在
 * <p>resource 表结构（对齐 WP011）：resource_id VARCHAR(64) PK, deleted TINYINT
 *
 * <p>注意：未通过 ResourceRepository 接口，避免引入 aipe-resource Boot 模块导致双 datasource 冲突
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class JdbcResourceLookupAdapter implements ResourceLookupPort {

    private static final Logger log = LoggerFactory.getLogger(JdbcResourceLookupAdapter.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** resource 表名（对齐 WP011 resource 表） */
    private static final String RESOURCE_TABLE = "resource";

    @Override
    public boolean exists(String resourceId) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return false;
        }
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + RESOURCE_TABLE + " WHERE resource_id = ? AND deleted = 0",
                    Integer.class, resourceId);
            return count != null && count > 0;
        } catch (Exception e) {
            log.warn("Resource lookup failed for id {}: {}", resourceId, e.getMessage());
            return false;
        }
    }
}
