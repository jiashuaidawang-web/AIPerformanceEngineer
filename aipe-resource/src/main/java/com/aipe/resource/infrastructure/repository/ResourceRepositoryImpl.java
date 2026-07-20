package com.aipe.resource.infrastructure.repository;

import com.aipe.resource.domain.Resource;
import com.aipe.resource.domain.ResourceId;
import com.aipe.resource.domain.ResourceRepository;
import com.aipe.resource.domain.ResourceStatus;
import com.aipe.resource.domain.ResourceType;
import com.aipe.resource.infrastructure.converter.ResourceConverter;
import com.aipe.resource.infrastructure.mapper.ResourceMapper;
import com.aipe.resource.infrastructure.po.ResourcePO;
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
 * 资源仓储实现
 *
 * <p>Gateway Law-003：Repository Implementation In Infrastructure
 * <p>Gateway Law-001：Returns Domain, Never PO
 * <p>Gateway Law-004：Repository Contains No Business Logic
 * <p>Gateway Law-005：Repository Is Not Transaction Owner
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Repository
public class ResourceRepositoryImpl implements ResourceRepository {

    private static final Logger log = LoggerFactory.getLogger(ResourceRepositoryImpl.class);

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public Resource save(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        ResourcePO po = ResourceConverter.toPO(resource);
        resourceMapper.insert(po);
        log.debug("Saved resource: id={}, type={}", po.getResourceId(), po.getResourceType());
        return ResourceConverter.toDomain(po);
    }

    @Override
    public Resource update(Resource resource) {
        if (resource == null) {
            throw new IllegalArgumentException("Resource cannot be null");
        }
        ResourcePO po = ResourceConverter.toPO(resource);
        int rows = resourceMapper.updateById(po);
        if (rows == 0) {
            throw new IllegalStateException("Resource update failed (optimistic lock?), id=" + po.getResourceId());
        }
        log.debug("Updated resource: id={}, version={}", po.getResourceId(), po.getVersion());
        // 重新查询以获取最新状态
        return findById(resource.getId())
                .orElseThrow(() -> new IllegalStateException("Resource not found after update, id=" + po.getResourceId()));
    }

    @Override
    public Optional<Resource> findById(ResourceId id) {
        if (id == null) {
            return Optional.empty();
        }
        ResourcePO po = resourceMapper.selectById(id.getValue());
        return Optional.ofNullable(ResourceConverter.toDomain(po));
    }

    @Override
    public List<Resource> findByBusinessSystem(String businessSystem) {
        if (businessSystem == null || businessSystem.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<ResourcePO> poList = resourceMapper.selectByBusinessSystem(businessSystem);
        return poList.stream()
                .map(ResourceConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> findByType(ResourceType type) {
        if (type == null) {
            return Collections.emptyList();
        }
        List<ResourcePO> poList = resourceMapper.selectByType(type.name());
        return poList.stream()
                .map(ResourceConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> findByStatus(ResourceStatus status) {
        if (status == null) {
            return Collections.emptyList();
        }
        List<ResourcePO> poList = resourceMapper.selectByStatus(status.name());
        return poList.stream()
                .map(ResourceConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> findByParentId(ResourceId parentId) {
        if (parentId == null) {
            return Collections.emptyList();
        }
        List<ResourcePO> poList = resourceMapper.selectByParentId(parentId.getValue());
        return poList.stream()
                .map(ResourceConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Resource> findAll() {
        QueryWrapper<ResourcePO> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        List<ResourcePO> poList = resourceMapper.selectList(wrapper);
        return poList.stream()
                .map(ResourceConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(ResourceId id) {
        if (id == null) {
            return false;
        }
        int rows = resourceMapper.deleteById(id.getValue());
        log.debug("Deleted resource: id={}, rows={}", id.getValue(), rows);
        return rows > 0;
    }

    @Override
    public long countByBusinessSystem(String businessSystem) {
        if (businessSystem == null || businessSystem.trim().isEmpty()) {
            return 0;
        }
        return resourceMapper.countByBusinessSystem(businessSystem);
    }
}
