package com.aipe.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI Performance Engineer - Backend Application
 *
 * <p>Control plane service for:
 * <ul>
 *   <li>Scenario Management (压测场景编排)</li>
 *   <li>Observation Query (观测数据查询)</li>
 *   <li>Metrics Dashboard (指标看板)</li>
 * </ul>
 */
@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
