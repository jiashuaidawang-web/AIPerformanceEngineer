package com.aipe.connector.sdk;

import com.aipe.common.domain.ObservationData;
import com.aipe.connector.sdk.context.ConnectorContext;
import com.aipe.connector.sdk.exception.ConnectorException;
import com.aipe.connector.sdk.lifecycle.ConnectorState;

import java.util.List;

/**
 * Connector SPI (Service Provider Interface)
 *
 * <p>所有数据采集器（JVM/Linux/Redis/MySQL）必须实现此接口。
 * Agent 通过此接口管理 Connector 生命周期，不依赖具体实现。
 *
 * <p>实现要求：
 * <ul>
 *   <li>所有采集逻辑必须真实实现（禁止 TODO / Mock / 空方法）</li>
 *   <li>Connector 异常不得导致 Agent 退出</li>
 *   <li>collect() 方法必须在超时时间内返回</li>
 * </ul>
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public interface Connector {

    /**
     * 返回 Connector 唯一标识
     *
     * @return connectorId
     */
    String getConnectorId();

    /**
     * 返回 Connector 类型
     *
     * @return 类型标识 (JVM/LINUX/REDIS/MYSQL)
     */
    String getConnectorType();

    /**
     * 返回采集目标资源标识
     *
     * @return 目标资源描述
     */
    String getTargetResource();

    /**
     * 初始化 Connector
     *
     * @param context Connector 上下文
     * @throws ConnectorException 初始化失败
     */
    void init(ConnectorContext context) throws ConnectorException;

    /**
     * 执行一次采集
     *
     * @return 采集结果列表（不可为 null）
     * @throws ConnectorException 采集失败
     */
    List<ObservationData> collect() throws ConnectorException;

    /**
     * 启动 Connector
     *
     * @throws ConnectorException 启动失败
     */
    void start() throws ConnectorException;

    /**
     * 停止 Connector
     */
    void stop();

    /**
     * 销毁 Connector
     */
    void destroy();

    /**
     * 返回 Connector 当前状态
     *
     * @return Connector 状态
     */
    ConnectorState getStatus();
}
