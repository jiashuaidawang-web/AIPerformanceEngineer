package com.aipe.observation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Observation Domain Engine - 启动类
 *
 * <p>WP012 Observation Engine Blueprint / M2-006 Observation Model Specification
 * <p>DDD 分层架构：Domain / Application / Infrastructure / API
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@SpringBootApplication
public class ObservationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ObservationApplication.class, args);
    }
}
