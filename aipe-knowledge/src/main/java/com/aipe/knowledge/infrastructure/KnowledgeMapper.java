package com.aipe.knowledge.infrastructure;

import com.aipe.knowledge.domain.KnowledgeType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Knowledge MyBatis Plus Mapper（MySQL）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<KnowledgePO> {

    @Select("SELECT * FROM knowledge WHERE id = #{id} ORDER BY version DESC")
    List<KnowledgePO> selectAllVersions(@Param("id") String id);

    @Select("SELECT * FROM knowledge WHERE id = #{id} ORDER BY version DESC LIMIT 1")
    KnowledgePO selectLatest(@Param("id") String id);

    @Select("SELECT * FROM knowledge WHERE knowledge_type = #{type} ORDER BY confidence DESC")
    List<KnowledgePO> selectByType(@Param("type") String type);

    @Select("SELECT * FROM knowledge WHERE evidence_id = #{evidenceId} ORDER BY version DESC")
    List<KnowledgePO> selectByEvidenceId(@Param("evidenceId") String evidenceId);

    @Select("SELECT * FROM knowledge WHERE confidence >= #{minConfidence} ORDER BY confidence DESC")
    List<KnowledgePO> selectByMinConfidence(@Param("minConfidence") double minConfidence);
}
