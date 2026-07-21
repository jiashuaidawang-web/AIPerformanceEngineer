package com.stress.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stress.demo.entity.Product;
import com.stress.demo.repository.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductMapper productMapper;

    public List<Product> findAll(int page, int size) {
        Page<Product> productPage = new Page<>(page, size);
        productMapper.selectPage(productPage, new LambdaQueryWrapper<Product>().orderByDesc(Product::getId));
        return productPage.getRecords();
    }

    public Product findById(Long id) {
        return productMapper.selectById(id);
    }

    public List<Product> search(String keyword) {
        return productMapper.fullTableScan(keyword);
    }

    @Cacheable(value = "products", key = "#id")
    public Product getCached(Long id) {
        return productMapper.selectById(id);
    }

    public Product create(Product product) {
        productMapper.insert(product);
        return product;
    }
}
