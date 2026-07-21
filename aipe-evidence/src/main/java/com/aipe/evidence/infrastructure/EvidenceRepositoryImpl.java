package com.aipe.evidence.infrastructure;

import com.aipe.evidence.domain.Evidence;
import com.aipe.evidence.domain.EvidenceId;
import com.aipe.evidence.domain.EvidenceRepository;
import com.aipe.evidence.domain.EvidenceStatus;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Evidence 仓储实现（MySQL）
 *
 * <p>Gateway Law-001/003/005
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Repository
public class EvidenceRepositoryImpl implements EvidenceRepository {

    private static final Logger log = LoggerFactory.getLogger(EvidenceRepositoryImpl.class);

    @Autowired
    private EvidenceMapper evidenceMapper;

    @Autowired
    private EvidenceConverter evidenceConverter;

    @Override
    public Evidence save(Evidence evidence) {
        if (evidence == null) throw new IllegalArgumentException("Evidence cannot be null");
        EvidencePO po = evidenceConverter.toPO(evidence);
        evidenceMapper.insert(po);
        log.debug("Saved evidence: id={}", po.getId());
        return evidenceConverter.toDomain(po);
    }

    @Override
    public Optional<Evidence> findById(EvidenceId id) {
        if (id == null) return Optional.empty();
        return findById(id.getValue());
    }

    @Override
    public Optional<Evidence> findById(String id) {
        if (id == null) return Optional.empty();
        EvidencePO po = evidenceMapper.selectById(id);
        return Optional.ofNullable(evidenceConverter.toDomain(po));
    }

    @Override
    public List<Evidence> findByRootResource(String resourceId) {
        if (resourceId == null) return java.util.Collections.emptyList();
        List<EvidencePO> poList = evidenceMapper.selectByRootResource(resourceId);
        return poList.stream().map(evidenceConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Evidence> findByStatus(EvidenceStatus status) {
        if (status == null) return java.util.Collections.emptyList();
        List<EvidencePO> poList = evidenceMapper.selectByStatus(status.name());
        return poList.stream().map(evidenceConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Evidence> findHighConfidence(double minConfidence) {
        List<EvidencePO> poList = evidenceMapper.selectHighConfidence(minConfidence);
        return poList.stream().map(evidenceConverter::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Evidence> findAll() {
        List<EvidencePO> poList = evidenceMapper.selectList(new QueryWrapper<>());
        return poList.stream().map(evidenceConverter::toDomain).collect(Collectors.toList());
    }    @Override
    public boolean updateStatus(EvidenceId id, EvidenceStatus newStatus) {
        if (id == null || newStatus == null) return false;
        int rows = evidenceMapper.updateStatus(id.getValue(), newStatus.name());
        log.debug("Updated evidence status: id={}, status={}, rows={}", id.getValue(), newStatus, rows);
        return rows > 0;    }

    @Override
    public long count() {
        return evidenceMapper.selectCount(new QueryWrapper<>());
    }
}
