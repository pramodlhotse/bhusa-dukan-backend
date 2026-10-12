package com.bhusadukan.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateDeliveryRequest(

        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "Delivery person ID is required")
        Long deliveryPersonId,

        @Size(max = 50, message = "Vehicle number must not exceed 50 characters")
        String vehicleNumber,

        @Size(max = 500, message = "Remarks must not exceed 500 characters")
        String remarks
) {
}