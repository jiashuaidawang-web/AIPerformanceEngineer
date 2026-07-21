package com.aipe.knowledge.infrastructure;

import com.aipe.knowledge.domain.Knowledge;
import com.aipe.knowledge.domain.KnowledgeId;
import com.aipe.knowledge.domain.KnowledgeRepository;
import com.aipe.knowledge.domain.KnowledgeType;
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
 * Knowledge 仓储实现（MySQL）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Repository
public class KnowledgeRepositoryImpl implements KnowledgeRepository {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeRepositoryImpl.class);

    @Autowired
    private KnowledgeMapper knowledgeMapper;

    @Autowired
    private KnowledgeConverter knowledgeConverter;

    @Override
    public Knowledge save(Knowledge knowledge) {
        if (knowledge == null) throw new IllegalArgumentException("Knowledge cannot be null");
        KnowledgePO po = knowledgeConverter.toPO(knowledge);
        knowledgeMapper.insert(po);
        log.debug("Saved knowledge: id={}, version={}", po.getId(), po.getVersion());
        return knowledgeConverter.toDomain(po);
    }

    @Override
    public Optional<Knowledge> findById(KnowledgeId id) {
        if (id == null) return Optional.empty();
        return findById(id.getValue());
    }

    @Override
    public Optional<Knowledge> findById(String id) {
        if (id == null) return Optional.empty();
        KnowledgePO po = knowledgeMapper.selectById(id);
        return Optional.ofNullable(knowledgeConverter.toDomain(po));
    }

    @Override
    public List<Knowledge> findAllVersions(KnowledgeId id) {
        if (id == null) return Collections.emptyList();
        List<KnowledgePO> poList = knowledgeMapper.selectAllVersions(id.getValue());
        return poList.stream().map(knowledgeConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Knowledge> findLatest(KnowledgeId id) {
        if (id == null) return Optional.empty();
        KnowledgePO po = knowledgeMapper.selectLatest(id.getValue());
        return Optional.ofNullable(knowledgeConverter.toDomain(po));
    }

    @Override
    public List<Knowledge> findByType(KnowledgeType type) {
        if (type == null) return Collections.emptyList();
        List<KnowledgePO> poList = knowledgeMapper.selectByType(type.name());
        return poList.stream().map(knowledgeConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Knowledge> findByEvidenceId(String evidenceId) {
        if (evidenceId == null) return Collections.emptyList();
        List<KnowledgePO> poList = knowledgeMapper.selectByEvidenceId(evidenceId);
        return poList.stream().map(knowledgeConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Knowledge> findByMinConfidence(double minConfidence) {
        List<KnowledgePO> poList = knowledgeMapper.selectByMinConfidence(minConfidence);
        return poList.stream().map(knowledgeConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Knowledge> findAll() {
        List<KnowledgePO> poList = knowledgeMapper.selectList(new QueryWrapper<>());
        return poList.stream().map(knowledgeConverter::toDomain).collect(Collectors.toList());
    }
}
