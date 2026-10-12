package com.bhusadukan.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InventoryTransactionResponse(
        Long id,
        Long productId,
        String productName,
        String transactionType,
        BigDecimal quantity,
        String referenceType,
        Long referenceId,
        String remarks,
        Long createdBy,
        LocalDateTime createdAt
) {
}