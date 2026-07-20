package com.aipe.observation.infrastructure;

import com.aipe.observation.domain.Observation;
import com.aipe.observation.domain.ObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Observation 仓储实现（ClickHouse）
 *
 * <p>Gateway Law-003：Repository Implementation In Infrastructure
 * <p>Gateway Law-001：Returns Domain, Never PO
 * <p>Gateway Law-004：Repository Contains No Business Logic
 * <p>Gateway Law-005：Repository Is Not Transaction Owner
 *
 * <p>实现说明：通过 ObservationMapper（原生 JDBC）操作 ClickHouse observation_fact 表，
 * PO ↔ Domain 转换由 ObservationConverter 负责（返回 Domain）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Repository
public class ObservationRepositoryImpl implements ObservationRepository {

    private static final Logger log = LoggerFactory.getLogger(ObservationRepositoryImpl.class);

    @Autowired
    private ObservationMapper observationMapper;

    @Override
    public Observation save(Observation observation) {
        if (observation == null) {
            throw new IllegalArgumentException("Observation cannot be null");
        }
        ObservationPO po = ObservationConverter.toPO(observation);
        observationMapper.insert(po);
        log.debug("Saved observation: id={}", po.getObservationId());
        return ObservationConverter.toDomain(po);
    }

    @Override
    public List<Observation> batchSave(List<Observation> observations) {
        if (observations == null || observations.isEmpty()) {
            return Collections.emptyList();
        }
        List<ObservationPO> poList = new ArrayList<>(observations.size());
        for (Observation observation : observations) {
            poList.add(ObservationConverter.toPO(observation));
        }
        observationMapper.batchInsert(poList);
        log.debug("Batch saved {} observations", observations.size());
        return poList.stream()
                .map(ObservationConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Observation> findByResourceId(String resourceId, int limit) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<ObservationPO> poList = observationMapper.selectByResourceId(resourceId, limit);
        return poList.stream()
                .map(ObservationConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Observation> findByResourceAndTimeRange(String resourceId, long startTime, long endTime, int limit) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<ObservationPO> poList = observationMapper.selectByResourceAndTimeRange(
                resourceId, Math.min(startTime, endTime), Math.max(startTime, endTime), limit);
        return poList.stream()
                .map(ObservationConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Observation> findByResourceAndMetric(String resourceId, String metricName, int limit) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        if (metricName == null || metricName.trim().isEmpty()) {
            return findByResourceId(resourceId, limit);
        }
        List<ObservationPO> poList = observationMapper.selectByResourceAndMetric(resourceId, metricName, limit);
        return poList.stream()
                .map(ObservationConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Observation> findByMetricAndTimeRange(String resourceId, String metricName,
                                                      long startTime, long endTime, int limit) {
        if (resourceId == null || resourceId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        if (metricName == null || metricName.trim().isEmpty()) {
            return findByResourceAndTimeRange(resourceId, startTime, endTime, limit);
        }
        List<ObservationPO> poList = observationMapper.selectByMetricAndTimeRange(
                resourceId, metricName, Math.min(startTime, endTime), Math.max(startTime, endTime), limit);
        return poList.stream()
                .map(ObservationConverter::toDomain)
                .collect(Collectors.toList());
    }
}
