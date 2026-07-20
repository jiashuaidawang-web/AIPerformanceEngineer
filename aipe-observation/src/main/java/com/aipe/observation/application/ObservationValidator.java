package com.aipe.observation.application;

import com.aipe.observation.domain.Observation;
import com.aipe.observation.domain.ObservationSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Observation 校验器
 *
 * <p>负责 Observation 入库前的业务校验（Orchestration Law-001：Application 层编排校验）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Component
public class ObservationValidator {

    private static final Logger log = LoggerFactory.getLogger(ObservationValidator.class);

    /** 时间戳合法范围：2020-01-01 ~ 2100-01-01（毫秒） */
    private static final long MIN_TIMESTAMP = 1577836800000L;
    private static final long MAX_TIMESTAMP = 4102444800000L;

    /**
     * 校验 Observation 是否合法
     *
     * @param observation 待校验 Observation
     * @throws IllegalArgumentException 校验失败
     */
    public void validate(Observation observation) {
        if (observation == null) {
            throw new IllegalArgumentException("Observation cannot be null");
        }
        ObservationSpecification.validateForCreate(observation);
        validateTimestamp(observation.getTimestamp());
    }

    /**
     * 校验 Observation 列表（批量）
     *
     * @param observations 列表
     * @throws IllegalArgumentException 任一校验失败
     */
    public void validateAll(java.util.List<Observation> observations) {
        if (observations == null || observations.isEmpty()) {
            throw new IllegalArgumentException("Observation list cannot be null or empty");
        }
        for (Observation observation : observations) {
            validate(observation);
        }
    }

    private void validateTimestamp(long timestamp) {
        if (timestamp < MIN_TIMESTAMP || timestamp > MAX_TIMESTAMP) {
            throw new IllegalArgumentException(
                    "Observation timestamp out of valid range: " + timestamp);
        }
    }
}
