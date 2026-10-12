package com.bhusadukan.dto;

import com.bhusadukan.entity.Delivery;
import jakarta.validation.constraints.NotNull;

public record UpdateDeliveryStatusRequest(

        @NotNull(message = "Status is required")
        Delivery.DeliveryStatus status,

        String failureReason,

        String remarks

) {
}