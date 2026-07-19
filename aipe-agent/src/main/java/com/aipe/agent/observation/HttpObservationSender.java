package com.aipe.agent.observation;

import com.aipe.agent.config.AgentConfig;
import com.aipe.common.domain.ObservationData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * HTTP Observation 发送器
 *
 * <p>将采集数据通过 HTTP POST 批量发送到 Backend。
 */
public class HttpObservationSender {
    private static final Logger log = LoggerFactory.getLogger(HttpObservationSender.class);
    private static final int DEFAULT_QUEUE_CAPACITY = 10000;
    private static final int BATCH_SIZE = 100;
    private static final long FLUSH_INTERVAL_MS = 5000L; // 5秒刷新一次

    private final AgentConfig config;
    private final BlockingQueue<ObservationData> queue;
    private final RestTemplate restTemplate;
    private final ExecutorService senderExecutor;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public HttpObservationSender(AgentConfig config) {
        this.config = config;
        this.queue = new LinkedBlockingQueue<>(DEFAULT_QUEUE_CAPACITY);
        this.restTemplate = new RestTemplate();
        this.senderExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "http-observation-sender");
            t.setDaemon(true);
            return t;
        });
    }

    public void init() {
        if (running.get()) return;
        running.set(true);
        senderExecutor.submit(this::consumeLoop);
        log.info("HttpObservationSender initialized, backendUrl={}", config.getBackendUrl());
    }

    public boolean send(List<ObservationData> observations) {
        if (!running.get()) return false;
        if (observations == null || observations.isEmpty()) return true;
        boolean allSuccess = true;
        for (ObservationData data : observations) {
            boolean offered = queue.offer(data);
            if (!offered) {
                allSuccess = false;
                log.warn("Observation queue full, dropping data");
            }
        }
        return allSuccess;
    }

    private void consumeLoop() {
        log.info("HttpObservationSender consume loop started");
        while (running.get() || !queue.isEmpty()) {
            try {
                List<ObservationData> batch = new ArrayList<>();
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
        log.info("HttpObservationSender consume loop ended");
    }

    private void flush(List<ObservationData> batch) {
        try {
            List<Map<String, Object>> observations = batch.stream()
                    .map(this::toMap)
                    .collect(Collectors.toList());

            Map<String, Object> request = new HashMap<>();
            request.put("agentId", config.getAgentId());
            request.put("connectorType", batch.get(0).getConnectorType());
            request.put("observations", observations);

            String url = config.getBackendUrl() + "/api/v1/observations/batch";
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            log.debug("Flushed {} observations to backend, response={}", batch.size(), response);
        } catch (Exception e) {
            log.error("Failed to flush observations to backend: {}", e.getMessage());
        }
    }

    private Map<String, Object> toMap(ObservationData data) {
        Map<String, Object> map = new HashMap<>();
        map.put("resource_id", data.getTargetResource());
        map.put("metric_name", data.getMetricName());
        map.put("metric_value", data.getMetricValue());
        map.put("timestamp", data.getCollectTime());
        map.put("tags", data.getTags() != null ? data.getTags().toString() : "{}");
        return map;
    }

    public void shutdown() {
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
        List<ObservationData> remaining = new ArrayList<>();
        queue.drainTo(remaining);
        if (!remaining.isEmpty()) {
            flush(remaining);
        }
    }
}
