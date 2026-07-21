package com.aipe.evidence;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Evidence Engine - 启动类
 *
 * <p>WP015 Evidence Engine Blueprint / M2-011 Evidence Model
 * <p>AI Principle-001: Evidence Before Conclusion
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.aipe.evidence.infrastructure")
public class EvidenceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvidenceApplication.class, args);
    }
}
