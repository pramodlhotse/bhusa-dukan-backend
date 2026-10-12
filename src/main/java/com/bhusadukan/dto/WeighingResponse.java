package com.bhusadukan.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WeighingResponse(
        Long id,
        Long orderId,
        Long orderItemId,
        BigDecimal orderedWeight,
        BigDecimal actualWeight,
        BigDecimal weighingMachineReading,
        String weighingMachineId,
        Long verifiedBy,
        LocalDateTime verifiedAt,
        String remarks,
        LocalDateTime createdAt
) {
}