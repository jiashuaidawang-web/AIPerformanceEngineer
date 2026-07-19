package com.aipe.agent.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 调度管理器
 *
 * <p>基于 ScheduledExecutorService 的定时任务调度。
 * 统一调度所有 Connector 的周期性采集任务。
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
public class SchedulerManager {

    private static final Logger log = LoggerFactory.getLogger(SchedulerManager.class);

    private final int poolSize;
    private ScheduledExecutorService scheduler;
    private final List<ScheduledFuture<?>> futures = new ArrayList<>();
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public SchedulerManager(int poolSize) {
        if (poolSize <= 0) {
            poolSize = 4;
        }
        this.poolSize = poolSize;
    }

    /**
     * 初始化调度器
     */
    public synchronized void init() {
        if (initialized.get()) {
            log.warn("SchedulerManager already initialized");
            return;
        }
        this.scheduler = Executors.newScheduledThreadPool(poolSize, r -> {
            Thread t = new Thread(r, "agent-scheduler-" + System.nanoTime());
            t.setDaemon(true);
            return t;
        });
        initialized.set(true);
        log.info("SchedulerManager initialized with poolSize={}", poolSize);
    }

    /**
     * 创建定时任务
     *
     * @param task         待执行任务
     * @param intervalMs   执行间隔（毫秒）
     */
    public void schedule(Runnable task, long intervalMs) {
        if (!initialized.get()) {
            throw new IllegalStateException("SchedulerManager not initialized. Call init() first.");
        }
        if (intervalMs <= 0) {
            intervalMs = 30000L;
        }
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
            try {
                task.run();
            } catch (Exception e) {
                log.error("Scheduled task execution failed", e);
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
        futures.add(future);
        log.debug("Task scheduled with interval={}ms", intervalMs);
    }

    /**
     * 关闭调度器
     */
    public synchronized void shutdown() {
        log.info("Shutting down SchedulerManager...");
        for (ScheduledFuture<?> future : futures) {
            if (future != null && !future.isCancelled()) {
                future.cancel(false);
            }
        }
        futures.clear();
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.warn("Scheduler did not terminate within timeout, forcing shutdown");
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                scheduler.shutdownNow();
            }
        }
        initialized.set(false);
        log.info("SchedulerManager shutdown complete");
    }

    /**
     * 返回当前线程池大小
     */
    public int getPoolSize() {
        return poolSize;
    }

    /**
     * 返回已调度任务数量
     */
    public int getScheduledTaskCount() {
        return futures.size();
    }

    public boolean isInitialized() {
        return initialized.get();
    }
}
