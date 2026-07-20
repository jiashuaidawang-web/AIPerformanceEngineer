package com.aipe.resource.infrastructure.mapper;

import com.aipe.resource.infrastructure.po.ResourcePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 资源 MyBatis Plus Mapper
 *
 * <p>基础 CRUD 由 BaseMapper 提供，复杂查询自定义
 *
 * @author AI Performance Engineer
 * @since 1.0.0
 */
@Mapper
public interface ResourceMapper extends BaseMapper<ResourcePO> {

    /**
     * 根据业务系统查询
     *
     * @param businessSystem 业务系统名
     * @return 资源 PO 列表
     */
    @Select("SELECT * FROM resource WHERE business_system = #{businessSystem} AND deleted = 0")
    List<ResourcePO> selectByBusinessSystem(@Param("businessSystem") String businessSystem);

    /**
     * 根据父资源 ID 查询子资源
     *
     * @param parentId 父资源 ID
     * @return 子资源 PO 列表
     */
    @Select("SELECT * FROM resource WHERE parent_resource_id = #{parentId} AND deleted = 0")
    List<ResourcePO> selectByParentId(@Param("parentId") String parentId);

    /**
     * 根据类型查询
     *
     * @param resourceType 资源类型
     * @return 资源 PO 列表
     */
    @Select("SELECT * FROM resource WHERE resource_type = #{resourceType} AND deleted = 0")
    List<ResourcePO> selectByType(@Param("resourceType") String resourceType);

    /**
     * 根据状态查询
     *
     * @param status 资源状态
     * @return 资源 PO 列表
     */
    @Select("SELECT * FROM resource WHERE resource_status = #{status} AND deleted = 0")
    List<ResourcePO> selectByStatus(@Param("status") String status);

    /**
     * 统计业务系统下的资源数量
     *
     * @param businessSystem 业务系统名
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM resource WHERE business_system = #{businessSystem} AND deleted = 0")
    long countByBusinessSystem(@Param("businessSystem") String businessSystem);
}
