package com.aipe.alert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 告警引擎 - Alert Engine
 *
 * <p>功能:
 * <ul>
 *   <li>告警规则 CRUD</li>
 *   <li>告警记录查询</li>
 *   <li>告警通知 (Webhook)</li>
 * </ul>
 */
@SpringBootApplication
public class AlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertApplication.class, args);
    }
}
