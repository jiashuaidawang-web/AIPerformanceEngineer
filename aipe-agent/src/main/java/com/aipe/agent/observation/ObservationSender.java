package com.aipe.agent.observation;

import com.aipe.agent.config.AgentConfig;
import com.aipe.common.domain.ObservationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 采集数据发送器
 *
 * <p>内部维护一个阻塞队列 + 消费线程，将 Observation 数据异步发送至后端 Pipeline。
 * MVP 阶段将数据发送至日志（结构完整，后续对接 HTTP/Kafka）。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class ObservationSender {

    private static final Logger log = LoggerFactory.getLogger(ObservationSender.class);
    private static final int DEFAULT_QUEUE_CAPACITY = 10000;
    private static final int BATCH_SIZE = 100;
    private static final long FLUSH_INTERVAL_MS = 1000L;

    private final AgentConfig config;
    private final BlockingQueue<ObservationData> queue;
    private final ExecutorService senderExecutor;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong totalSent = new AtomicLong(0);
    private final AtomicLong totalDropped = new AtomicLong(0);

    public ObservationSender(AgentConfig config) {
        this.config = config;
        this.queue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
        this.senderExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "observation-sender");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * 初始化发送器
     */
    public void init() {
        if (running.get()) {
            log.warn("ObservationSender already running");
            return;
        }
        running.set(true);
        senderExecutor.submit(this::consumeLoop);
        log.info("ObservationSender initialized, backendUrl={}", config.getBackendUrl());
    }

    /**
     * 发送采集数据（入队）
     *
     * @param observations 采集数据列表
     * @return 是否成功入队
     */
    public boolean send(List<ObservationData> observations) {
        if (!running.get()) {
            log.warn("ObservationSender is not running, dropping observations");
            totalDropped.incrementAndGet();
            return false;
        }
        if (observations == null || observations.isEmpty()) {
            return true;
        }
        boolean allSuccess = true;
        for (ObservationData data : observations) {
            boolean offered = queue.offer(data);
            if (!offered) {
                totalDropped.incrementAndGet();
                allSuccess = false;
                if (totalDropped.get() % 1000 == 1) {
                    log.warn("Observation queue full, dropping data. totalDropped={}", totalDropped.get());
                }
            }
        }
        return allSuccess;
    }

    /**
     * 消费循环
     */
    private void consumeLoop() {
        log.info("ObservationSender consume loop started");
        while (running.get() || !queue.isEmpty()) {
            try {
                List<ObservationData> batch = new java.util.ArrayList<>();
                ObservationData first = queue.poll(FLUSH_INTERVAL_MS, TimeUnit.MILLISECONDS);
                if (first != null) {
                    batch.add(first);
                    queue.drainTo(batch, BATCH_SIZE - 1);
                }
                if (!batch.isEmpty()) {
                    flush(batch);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("Error in observation consume loop", e);
            }
        }
        log.info("ObservationSender consume loop ended");
    }

    /**
     * 批量发送
     */
    private void flush(List<ObservationData> batch) {
        // MVP: 后续替换为 HTTP/Kafka 发送
        log.info("Flushing observations to backend: count={}, backendUrl={}", batch.size(), config.getBackendUrl());
        for (ObservationData data : batch) {
            log.debug("  -> metric={}, value={}, unit={}, resource={}",
                    data.getMetricName(), data.getMetricValue(), data.getUnit(), data.getTargetResource());
        }
        totalSent.addAndGet(batch.size());
    }

    /**
     * 关闭发送器
     */
    public void shutdown() {
        log.info("Shutting down ObservationSender...");
        running.set(false);
        senderExecutor.shutdown();
        try {
            if (!senderExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                senderExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            senderExecutor.shutdownNow();
        }
        // 发送剩余数据
        List<ObservationData> remaining = new java.util.ArrayList<>();
        queue.drainTo(remaining);
        if (!remaining.isEmpty()) {
            flush(remaining);
        }
        log.info("ObservationSender shutdown complete. totalSent={}, totalDropped={}",
                totalSent.get(), totalDropped.get());
    }

    public long getTotalSent() {
        return totalSent.get();
    }

    public long getTotalDropped() {
        return totalDropped.get();
    }

    public int getQueueSize() {
        return queue.size();
    }

    public boolean isRunning() {
        return running.get();
    }
}
