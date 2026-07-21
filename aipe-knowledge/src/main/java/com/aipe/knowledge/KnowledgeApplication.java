package com.aipe.knowledge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Knowledge Engine - 启动类
 *
 * <p>WP016 Knowledge Engine Blueprint / M2-012 Knowledge Model
 * <p>Domain Law-001: Knowledge Is Verified Evidence
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.aipe.knowledge.infrastructure")
public class KnowledgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowledgeApplication.class, args);
    }
}
