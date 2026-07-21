package com.aipe.recommendation.infrastructure;

import com.aipe.recommendation.domain.RecommendationStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Recommendation MyBatis Plus Mapper（MySQL）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Mapper
public interface RecommendationMapper extends BaseMapper<RecommendationPO> {

    @Select("SELECT * FROM recommendation WHERE target_resource_id = #{resourceId} ORDER BY created_at DESC")
    List<RecommendationPO> selectByResource(@Param("resourceId") String resourceId);

    @Select("SELECT * FROM recommendation WHERE status = #{status} ORDER BY created_at DESC")
    List<RecommendationPO> selectByStatus(@Param("status") String status);

    @Select("SELECT * FROM recommendation WHERE knowledge_id = #{knowledgeId} ORDER BY created_at DESC")
    List<RecommendationPO> selectByKnowledgeId(@Param("knowledgeId") String knowledgeId);

    @Select("SELECT * FROM recommendation WHERE confidence >= #{minConfidence} AND priority = 'HIGH' ORDER BY confidence DESC")
    List<RecommendationPO> selectHighPriority(@Param("minConfidence") double minConfidence);

    @Update("UPDATE recommendation SET status = #{status}, updated_at = NOW(), version = version + 1 WHERE id = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") String status);
}
