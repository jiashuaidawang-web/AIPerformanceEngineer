package com.aipe.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI Performance Engineer - Config Manager Application
 *
 * <p>Backend service for:
 * <ul>
 *   <li>Agent Registration and Management</li>
 *   <li>Configuration Center</li>
 *   <li>Deployment Management</li>
 *   <li>Heartbeat Monitoring</li>
 * </ul>
 */
@SpringBootApplication
public class ConfigManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConfigManagerApplication.class, args);
    }
}
