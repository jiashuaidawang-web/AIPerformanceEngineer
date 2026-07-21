package com.aipe.execution.infrastructure;

import com.aipe.execution.domain.ExecutionStatus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * Execution MyBatis Plus Mapper（MySQL）
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Mapper
public interface ExecutionMapper extends BaseMapper<ExecutionPO> {

    @Select("SELECT * FROM execution WHERE recommendation_id = #{recommendationId} ORDER BY created_at DESC")
    List<ExecutionPO> selectByRecommendation(@Param("recommendationId") String recommendationId);

    @Select("SELECT * FROM execution WHERE status = #{status} ORDER BY created_at DESC")
    List<ExecutionPO> selectByStatus(@Param("status") String status);

    @Select("SELECT * FROM execution WHERE executor = #{executor} ORDER BY created_at DESC")
    List<ExecutionPO> selectByExecutor(@Param("executor") String executor);

    @Update("UPDATE execution SET status = #{status}, updated_at = NOW(), version = version + 1 WHERE id = #{id}")
    int updateStatus(@Param("id") String id, @Param("status") String status);
}
