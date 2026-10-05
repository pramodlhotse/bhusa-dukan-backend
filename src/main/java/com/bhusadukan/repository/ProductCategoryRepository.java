package com.bhusadukan.repository;

import com.bhusadukan.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository
        extends JpaRepository<ProductCategory, Long> {

    boolean existsByNameIgnoreCase(String name);
}