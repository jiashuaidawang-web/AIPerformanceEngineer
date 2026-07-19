package com.aipe.agent.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Agent 内部事件通信总线
 *
 * <p>实现简单发布-订阅机制，解耦 Agent 各模块之间的通信。
 * 所有事件为异步处理，订阅者异常不影响其他订阅者。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class AgentEventBus {

    private static final Logger log = LoggerFactory.getLogger(AgentEventBus.class);

    private final Map<String, List<Consumer<EventPayload>>> subscribers = new ConcurrentHashMap<>();
    private volatile boolean initialized = false;

    /**
     * 事件类型常量
     */
    public static final String CONNECTOR_REGISTERED = "connector.registered";
    public static final String CONNECTOR_INITIALIZED = "connector.initialized";
    public static final String CONNECTOR_STARTED = "connector.started";
    public static final String CONNECTOR_STOPPED = "connector.stopped";
    public static final String CONNECTOR_FAILED = "connector.failed";
    public static final String OBSERVATION_COLLECTED = "observation.collected";
    public static final String OBSERVATION_SENT = "observation.sent";
    public static final String OBSERVATION_SEND_FAILED = "observation.send.failed";
    public static final String AGENT_STATE_CHANGED = "agent.state.changed";

    /**
     * 初始化事件总线
     */
    public void init() {
        subscribers.clear();
        initialized = true;
        log.info("AgentEventBus initialized");
    }

    /**
     * 订阅事件
     *
     * @param eventType 事件类型
     * @param handler   事件处理器
     */
    public void subscribe(String eventType, Consumer<EventPayload> handler) {
        if (eventType == null || handler == null) {
            throw new IllegalArgumentException("eventType and handler must not be null");
        }
        subscribers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
        log.debug("Subscribed to event: {}", eventType);
    }

    /**
     * 取消订阅
     *
     * @param eventType 事件类型
     * @param handler   事件处理器
     */
    public void unsubscribe(String eventType, Consumer<EventPayload> handler) {
        if (eventType == null || handler == null) {
            return;
        }
        List<Consumer<EventPayload>> handlers = subscribers.get(eventType);
        if (handlers != null) {
            handlers.remove(handler);
        }
    }

    /**
     * 发布事件
     *
     * @param eventType 事件类型
     * @param payload   事件载荷
     */
    public void publish(String eventType, Object payload) {
        if (!initialized) {
            log.warn("EventBus not initialized, dropping event: {}", eventType);
            return;
        }
        List<Consumer<EventPayload>> handlers = subscribers.get(eventType);
        if (handlers == null || handlers.isEmpty()) {
            log.debug("No subscribers for event: {}", eventType);
            return;
        }
        EventPayload eventPayload = new EventPayload(eventType, payload, System.currentTimeMillis());
        for (Consumer<EventPayload> handler : handlers) {
            try {
                handler.accept(eventPayload);
            } catch (Exception e) {
                log.error("Event handler failed for type={}", eventType, e);
            }
        }
    }

    /**
     * 返回已注册的事件类型数
     */
    public int getSubscribedEventCount() {
        return subscribers.size();
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 事件载荷
     */
    public static class EventPayload {
        private final String eventType;
        private final Object payload;
        private final long timestamp;

        public EventPayload(String eventType, Object payload, long timestamp) {
            this.eventType = eventType;
            this.payload = payload;
            this.timestamp = timestamp;
        }

        public String getEventType() {
            return eventType;
        }

        public Object getPayload() {
            return payload;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
