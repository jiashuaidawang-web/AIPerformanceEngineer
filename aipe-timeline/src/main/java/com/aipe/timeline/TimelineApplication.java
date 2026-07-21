package com.aipe.timeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;

/**
 * Timeline Engine - 启动类
 *
 * <p>WP014 Timeline Engine Blueprint / M2-010 Timeline Model
 * <p>Persistence Law-004：Timeline Is Computed, Never Stored
 * <p>排除默认 DataSourceAutoConfiguration（只用 ClickHouse datasource）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
public class TimelineApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimelineApplication.class, args);
    }
}
