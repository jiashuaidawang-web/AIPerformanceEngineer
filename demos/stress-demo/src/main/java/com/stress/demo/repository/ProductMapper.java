package com.stress.demo.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stress.demo.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    /**
     * 全表扫描 — 模拟慢查询瓶颈
     */
    @Select("SELECT * FROM stress_product WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<Product> fullTableScan(@Param("keyword") String keyword);
}
