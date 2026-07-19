package com.aipe.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * 采集数据实体 - 采集器上报的最小数据单元
 * 
 * <p>所有Connector（JVM/Linux/Redis/MySQL）采集到的指标数据，
 * 最终都会封装为本实体，由Agent发送至Pipeline。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObservationData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Agent唯一标识
     */
    private String agentId;

    /**
     * Connector唯一标识
     */
    private String connectorId;

    /**
     * Connector类型 (JVM/LINUX/REDIS/MYSQL)
     */
    private String connectorType;

    /**
     * 采集目标资源标识 (如主机名、Redis地址、MySQL地址)
     */
    private String targetResource;

    /**
     * 采集时间戳 (毫秒)
     */
    private Long collectTime;

    /**
     * 指标名称
     */
    private String metricName;

    /**
     * 指标值
     */
    private Double metricValue;

    /**
     * 指标单位 (如 ms, %, bytes, count)
     */
    private String unit;

    /**
     * 扩展标签（维度信息）
     */
    private Map<String, String> tags;

    /**
     * 原始数据（各Connector自行填充，结构化字段）
     */
    private Map<String, Object> rawData;
}
