package com.aipe.resource;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Resource Domain Module - 启动类
 *
 * <p>WP011 Unified Resource Model
 * <p>DDD 分层架构：Domain / Application / Infrastructure / API
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@SpringBootApplication
@MapperScan("com.aipe.resource.infrastructure.mapper")
public class ResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
    }
}
