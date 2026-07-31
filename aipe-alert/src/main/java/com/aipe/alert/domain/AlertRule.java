package com.aipe.alert.domain;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 告警规则
 */
@Data
public class AlertRule implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 规则 ID */
    private String ruleId;

    /** 规则名称 */
    private String ruleName;

    /** 资源 ID (为空表示全局规则) */
    private String resourceId;

    /** 指标名称 */
    private String metricName;

    /** 条件: GT(大于), LT(小于), GTE(大于等于), LTE(小于等于) */
    private String condition;

    /** 阈值 */
    private Double threshold;

    /** 持续时间(秒), 持续超过阈值才告警 */
    private Integer duration;

    /** 告警级别: P0(紧急), P1(严重), P2(警告), P3(提示) */
    private String severity;

    /** 通知方式: webhook, email, dingtalk, wecom */
    private String notifyType;

    /** 通知地址 (webhook URL / 邮箱 / 群机器人) */
    private String notifyTarget;

    /** 是否启用 */
    private Boolean enabled;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
