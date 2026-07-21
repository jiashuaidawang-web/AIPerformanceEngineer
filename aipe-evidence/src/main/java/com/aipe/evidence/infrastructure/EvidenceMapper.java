package com.aipe.evidence.infrastructure;

import com.aipe.evidence.domain.EvidenceStatus;
import com.aipe.evidence.domain.EvidenceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Evidence MyBatis Plus Mapper（MySQL）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Mapper
public interface EvidenceMapper extends BaseMapper<EvidencePO> {

    @Select("SELECT * FROM evidence WHERE root_resource_id = #{rootResourceId} ORDER BY created_at DESC")
    List<EvidencePO> selectByRootResource(@Param("rootResourceId") String rootResourceId);

    @Select("SELECT * FROM evidence WHERE status = #{status} ORDER BY confidence DESC")
    List<EvidencePO> selectByStatus(@Param("status") String status);

    @Select("SELECT * FROM evidence WHERE confidence >= #{minConfidence} ORDER BY confidence DESC")
    List<EvidencePO> selectHighConfidence(@Param("minConfidence") double minConfidence);

    @Update("UPDATE evidence SET status = #{status}, updated_at = NOW(), version = version + 1 WHERE id = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") String status);
}
