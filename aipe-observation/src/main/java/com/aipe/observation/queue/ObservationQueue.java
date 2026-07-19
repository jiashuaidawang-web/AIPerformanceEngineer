package com.aipe.observation.queue;

import com.aipe.observation.model.Observation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ObservationQueue {
    private static final Logger log = LoggerFactory.getLogger(ObservationQueue.class);
    private static final int DEFAULT_CAPACITY = 50000;

    private final BlockingQueue<Observation> queue;

    public ObservationQueue() {
        this(DEFAULT_CAPACITY);
    }

    public ObservationQueue(int capacity) {
        this.queue = new LinkedBlockingQueue<>(capacity);
    }

    public void offer(Observation observation) {
        boolean accepted = queue.offer(observation);
        if (!accepted) {
            log.warn("Observation queue full, dropping observation: id={}", observation.getId());
        }
    }

    public Observation poll(long timeoutMs) throws InterruptedException {
        return queue.poll(timeoutMs, TimeUnit.MILLISECONDS);
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
