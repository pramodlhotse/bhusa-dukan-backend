package com.bhusadukan.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        String productName,
        BigDecimal orderedQuantity,
        BigDecimal actualQuantity,
        BigDecimal unitPrice,
        BigDecimal estimatedAmount,
        BigDecimal finalAmount
) {
}