package com.bhusadukan.dto;

import java.time.LocalDateTime;

public record OrderStatusHistoryResponse(
        Long id,
        String oldStatus,
        String newStatus,
        Long changedBy,
        String remarks,
        LocalDateTime createdAt
) {
}