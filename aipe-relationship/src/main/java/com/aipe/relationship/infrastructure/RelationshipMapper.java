package com.aipe.relationship.infrastructure;

import com.aipe.relationship.infrastructure.RelationshipPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Relationship MyBatis Plus Mapper
 *
 * <p>基础 CRUD 由 BaseMapper 提供，复杂查询自定义
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Mapper
public interface RelationshipMapper extends BaseMapper<RelationshipPO> {

    /**
     * 根据源 Resource ID 查询
     */
    @Select("SELECT * FROM relationship WHERE source_resource_id = #{sourceId} AND status = 'ACTIVE'")
    List<RelationshipPO> selectBySource(@Param("sourceId") String sourceId);

    /**
     * 根据目标 Resource ID 查询
     */
    @Select("SELECT * FROM relationship WHERE target_resource_id = #{targetId} AND status = 'ACTIVE'")
    List<RelationshipPO> selectByTarget(@Param("targetId") String targetId);

    /**
     * 按类型查询
     */
    @Select("SELECT * FROM relationship WHERE relationship_type = #{type} AND status = 'ACTIVE'")
    List<RelationshipPO> selectByType(@Param("type") String type);

    /**
     * 查询 Resource 的邻居（Source 或 Target 匹配）
     */
    @Select("SELECT * FROM relationship WHERE (source_resource_id = #{resourceId} OR target_resource_id = #{resourceId}) AND status = 'ACTIVE'")
    List<RelationshipPO> selectNeighbors(@Param("resourceId") String resourceId);

    /**
     * 查询 Resource 的全部 Relationship（Source + Target 双方）
     */
    @Select("SELECT * FROM relationship WHERE (source_resource_id = #{resourceId} OR target_resource_id = #{resourceId})")
    List<RelationshipPO> selectByResource(@Param("resourceId") String resourceId);

    /**
     * 查询所有 ACTIVE Relationship
     */
    @Select("SELECT * FROM relationship WHERE status = 'ACTIVE'")
    List<RelationshipPO> selectAllActive();

    /**
     * 统计 Resource 的 Relationship 数量
     */
    @Select("SELECT COUNT(*) FROM relationship WHERE (source_resource_id = #{resourceId} OR target_resource_id = #{resourceId})")
    long countByResource(@Param("resourceId") String resourceId);
}
