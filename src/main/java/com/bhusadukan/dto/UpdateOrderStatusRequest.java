package com.bhusadukan.dto;

import com.bhusadukan.entity.Order;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateOrderStatusRequest(

        @NotNull(message = "New status is required")
        Order.OrderStatus newStatus,

        @Size(max = 500, message = "Remarks must not exceed 500 characters")
        String remarks,

        Long changedBy
) {
}