package com.aipe.storage;

import com.aipe.observation.model.Observation;
import com.aipe.storage.writer.StorageRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageService {
    private static final Logger log = LoggerFactory.getLogger(StorageService.class);
    private final StorageRouter storageRouter;

    public StorageService(StorageRouter storageRouter) {
        this.storageRouter = storageRouter;
    }

    public void save(Observation observation) {
        storageRouter.route(observation);
    }

    public void batchSave(java.util.List<Observation> observations) {
        for (Observation obs : observations) {
            save(obs);
        }
    }

    public void init() {
        log.info("StorageService initialized");
    }
}
