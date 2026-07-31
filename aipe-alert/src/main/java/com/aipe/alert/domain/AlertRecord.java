package com.aipe.alert.domain;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 告警记录
 */
@Data
public class AlertRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 告警 ID */
    private String alertId;

    /** 规则 ID */
    private String ruleId;

    /** 资源 ID */
    private String resourceId;

    /** 指标名称 */
    private String metricName;

    /** 触发值 */
    private Double triggerValue;

    /** 阈值 */
    private Double threshold;

    /** 告警级别 */
    private String severity;

    /** 告警消息 */
    private String message;

    /** 状态: FIRING(触发中), RESOLVED(已恢复) */
    private String status;

    /** 触发时间 */
    private LocalDateTime triggeredAt;

    /** 恢复时间 */
    private LocalDateTime resolvedAt;
}
