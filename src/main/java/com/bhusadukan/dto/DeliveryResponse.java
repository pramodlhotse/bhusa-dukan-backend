package com.bhusadukan.dto;

import java.time.LocalDateTime;

public record DeliveryResponse(
        Long id,
        Long orderId,
        String orderNumber,
        Long deliveryPersonId,
        String deliveryPersonName,
        String vehicleNumber,
        String status,
        LocalDateTime assignedAt,
        LocalDateTime dispatchedAt,
        LocalDateTime deliveredAt,
        LocalDateTime failedAt,
        String failureReason,
        Boolean customerConfirmed,
        String remarks,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}