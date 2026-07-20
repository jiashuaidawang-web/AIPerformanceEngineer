package com.aipe.observation.application;

import com.aipe.observation.domain.Observation;
import com.aipe.observation.domain.ObservationFactory;
import com.aipe.observation.domain.ObservationRepository;
import com.aipe.observation.domain.ObservationSource;
import com.aipe.observation.domain.ObservationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Observation Pipeline 编排
 *
 * <p>负责 Observation 接收 → 校验 → 入库的编排（对齐 IM-003 / WP012 Blueprint §8）
 * <p>Law-001：Observation 的所有入库操作必须通过本 Pipeline，禁止直接操作 ObservationRepository
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ObservationPipeline {

    private static final Logger log = LoggerFactory.getLogger(ObservationPipeline.class);

    @Autowired
    private ObservationValidator validator;

    @Autowired
    private ObservationRepository observationRepository;

    /**
     * 处理单条 Observation 入库（接收 → 校验 → 入库 编排）
     *
     * @param observation Observation 聚合根
     * @return 入库结果     */
    public ObservationIncomingResult processIncoming(Observation observation) {
        if (observation == null) {
            return ObservationIncomingResult.fail("Observation is null");
        }
        try {
            validator.validate(observation);
        } catch (IllegalArgumentException e) {
            log.warn("Observation validation failed: {}", e.getMessage());
            return ObservationIncomingResult.fail(e.getMessage());
        }

        Observation saved;
        try {
            saved = observationRepository.save(observation);
        } catch (Exception e) {
            log.error("Failed to save observation: id={}, error={}", observation.getObservationId(), e.getMessage(), e);
            return ObservationIncomingResult.fail("Save failed: " + e.getMessage());
        }

        return ObservationIncomingResult.success(saved.getObservationId().getValue());
    }
}
