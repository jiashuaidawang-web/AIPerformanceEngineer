package com.aipe.recommendation.infrastructure;

import com.aipe.recommendation.domain.Recommendation;
import com.aipe.recommendation.domain.RecommendationId;
import com.aipe.recommendation.domain.RecommendationRepository;
import com.aipe.recommendation.domain.RecommendationStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Recommendation 仓储实现（MySQL）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Repository
public class RecommendationRepositoryImpl implements RecommendationRepository {

    private static final Logger log = LoggerFactory.getLogger(RecommendationRepositoryImpl.class);

    @Autowired
    private RecommendationMapper recommendationMapper;

    @Autowired
    private RecommendationConverter recommendationConverter;

    @Override
    public Recommendation save(Recommendation recommendation) {
        if (recommendation == null) throw new IllegalArgumentException("Recommendation cannot be null");
        RecommendationPO po = recommendationConverter.toPO(recommendation);
        recommendationMapper.insert(po);
        log.debug("Saved recommendation: id={}", po.getId());
        return recommendationConverter.toDomain(po);
    }

    @Override
    public Optional<Recommendation> findById(RecommendationId id) {
        if (id == null) return Optional.empty();
        return findById(id.getValue());
    }

    @Override
    public Optional<Recommendation> findById(String id) {
        if (id == null) return Optional.empty();
        RecommendationPO po = recommendationMapper.selectById(id);
        return Optional.ofNullable(recommendationConverter.toDomain(po));
    }

    @Override
    public List<Recommendation> findByResource(String resourceId) {
        if (resourceId == null) return Collections.emptyList();
        List<RecommendationPO> poList = recommendationMapper.selectByResource(resourceId);
        return poList.stream().map(recommendationConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Recommendation> findByStatus(RecommendationStatus status) {
        if (status == null) return Collections.emptyList();
        List<RecommendationPO> poList = recommendationMapper.selectByStatus(status.name());
        return poList.stream().map(recommendationConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Recommendation> findByKnowledgeId(String knowledgeId) {
        if (knowledgeId == null) return Collections.emptyList();
        List<RecommendationPO> poList = recommendationMapper.selectByKnowledgeId(knowledgeId);
        return poList.stream().map(recommendationConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Recommendation> findHighPriority(double minConfidence) {
        List<RecommendationPO> poList = recommendationMapper.selectHighPriority(minConfidence);
        return poList.stream().map(recommendationConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Recommendation> findAll() {
        List<RecommendationPO> poList = recommendationMapper.selectList(new QueryWrapper<>());
        return poList.stream().map(recommendationConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public boolean updateStatus(RecommendationId id, RecommendationStatus newStatus) {
        if (id == null || newStatus == null) return false;
        int rows = recommendationMapper.updateStatus(id.getValue(), newStatus.name());
        log.debug("Updated recommendation status: id={}, status={}, rows={}", id.getValue(), newStatus, rows);
        return rows > 0;
    }
}
