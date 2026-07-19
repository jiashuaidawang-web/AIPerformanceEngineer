package com.aipe.connector.sdk.abstractapi;

import com.aipe.connector.sdk.config.ConnectorConfig;
import com.aipe.connector.sdk.context.ConnectorContext;
import com.aipe.connector.sdk.context.ObservationEmitter;
import com.aipe.connector.sdk.exception.ConnectorException;
import com.aipe.connector.sdk.Connector;import com.aipe.connector.sdk.lifecycle.ConnectorState;
import com.aipe.connector.sdk.model.ConnectorMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Connector 抽象基类
 *
 * <p>所有 Connector 实现应继承此类，获得状态管理、生命周期控制、日志等基础能力。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public abstract class AbstractConnector implements Connector {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private volatile ConnectorState state = ConnectorState.CREATED;
    private ConnectorContext context;
    private ConnectorConfig config;

    @Override
    public void init(ConnectorContext context) throws ConnectorException {
        this.context = context;
        this.config = context != null ? context.getConfig() : null;
        this.state = ConnectorState.INITIALIZED;
        onInit(context);
        log.info("Connector initialized: id={}, type={}", getConnectorId(), getConnectorType());
    }

    @Override
    public void start() throws ConnectorException {
        if (!state.canStart()) {
            throw new ConnectorException(getConnectorId(), "Cannot start from state: " + state, null);
        }
        this.state = ConnectorState.STARTING;
        try {
            onStart();
            this.state = ConnectorState.RUNNING;
            log.info("Connector started: id={}", getConnectorId());
        } catch (Exception e) {
            this.state = ConnectorState.ERROR;
            throw new ConnectorException(getConnectorId(), "Start failed", e);
        }
    }

    @Override
    public void stop() {
        if (!state.canStop()) {
            log.warn("Cannot stop from state: {}", state);
            return;
        }
        this.state = ConnectorState.STOPPING;
        try {
            onStop();
            this.state = ConnectorState.STOPPED;
            log.info("Connector stopped: id={}", getConnectorId());
        } catch (Exception e) {
            this.state = ConnectorState.ERROR;
            log.error("Error stopping connector: id={}", getConnectorId(), e);
        }
    }

    @Override
    public void destroy() {
        this.state = ConnectorState.STOPPED;
        onDestroy();
        log.info("Connector destroyed: id={}", getConnectorId());
    }

    @Override
    public ConnectorState getStatus() {
        return state;
    }

    /**
     * 发送采集数据（供子类调用）
     */
    protected void emit(java.util.List<com.aipe.common.domain.ObservationData> observations) {
        if (context != null && context.getObservationSender() != null) {
            context.getObservationSender().send(observations);
        }
    }

    /**
     * 初始化钩子 - 子类覆盖
     */
    protected void onInit(ConnectorContext context) throws ConnectorException {}

    /**
     * 启动钩子 - 子类覆盖
     */
    protected void onStart() throws ConnectorException {}

    /**
     * 停止钩子 - 子类覆盖
     */
    protected void onStop() {}

    /**
     * 销毁钩子 - 子类覆盖
     */
    protected void onDestroy() {}

    public ConnectorContext getContext() { return context; }
    public ConnectorConfig getConfig() { return config; }
}
