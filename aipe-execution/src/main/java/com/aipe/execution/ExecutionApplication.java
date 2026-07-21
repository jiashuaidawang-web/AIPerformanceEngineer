package com.aipe.execution;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.aipe.execution.infrastructure")
public class ExecutionApplication {
    public static void main(String[] args) {
        SpringApplication.run(ExecutionApplication.class, args);
    }
}
