package com.stress.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableCaching
@MapperScan("com.stress.demo.repository")
public class StressDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(StressDemoApplication.class, args);
    }
}
