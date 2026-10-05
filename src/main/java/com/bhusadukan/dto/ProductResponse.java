package com.bhusadukan.dto;

import com.bhusadukan.entity.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        String description,
        Product.Unit unit,
        BigDecimal minimumQuantity,
        BigDecimal price,
        BigDecimal availableStock,
        boolean active
) {
}