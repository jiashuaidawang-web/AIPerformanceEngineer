package com.aipe.observation.config;

import org.springframework.context.annotation.Configuration;

/**
 * 占位配置（Observation 使用原生 JDBC 直连 ClickHouse，无需 MyBatis）
 *
 *<p>保留此 package 是为了满足 Blueprint §3 的 Package List：config/
 * <p>Observation 不引入 MyBatis Plus（避免与 ClickHouse 冲突）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Configuration
public class MyBatisConfig {
    // ObservationRepository 使用 JDBC 直连 ClickHouse（对齐 aipe-storage.ClickHouseClient）
    // 不启用 MyBatis Plus（避免自动注入与 ClickHouse 不兼容）
}
