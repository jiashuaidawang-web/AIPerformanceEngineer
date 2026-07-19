package com.aipe.observation.processor;

import com.aipe.observation.model.Observation;
import java.util.ArrayList;
import java.util.List;

public class ObservationBatcher {
    private final int batchSize;
    private final List<Observation> buffer;

    public ObservationBatcher(int batchSize) {
        this.batchSize = batchSize;
        this.buffer = new ArrayList<>(batchSize);
    }

    public synchronized void add(Observation observation) {
        buffer.add(observation);
    }

    public synchronized boolean isReady() {
        return buffer.size() >= batchSize;
    }

    public synchronized List<Observation> drain() {
        List<Observation> batch = new ArrayList<>(buffer);
        buffer.clear();
        return batch;
    }

    public synchronized int size() {
        return buffer.size();
    }
}
