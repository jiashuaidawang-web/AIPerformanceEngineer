package com.aipe.relationship;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Relationship + Topology Domain Engine - 启动类
 *
 * <p>WP013 Relationship + Topology Blueprint / M2-008 / M2-009
 * <p>DDD 分层 + GraphTopology：Relationship 存储于 MySQL，Topology 实时投影（不存储）
 * <p>Resource 存在性校验通过 ResourceLookupPort (JdbcTemplate 直读 resource 表)，不依赖 aipe-resource
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.aipe.relationship.infrastructure")
public class RelationshipApplication {

    public static void main(String[] args) {
        SpringApplication.run(RelationshipApplication.class, args);
    }
}
