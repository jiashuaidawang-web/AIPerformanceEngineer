package com.aipe.recommendation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Recommendation Engine - 启动类
 *
 * <p>WP017 Recommendation Engine Blueprint / M2-013 Optimization Model
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.aipe.recommendation.infrastructure")
public class RecommendationApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecommendationApplication.class, args);
    }
}
