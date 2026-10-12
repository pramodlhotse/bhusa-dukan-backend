
package com.bhusadukan.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateActualWeightRequest(
        @NotNull(message = "Order item ID is required")
        @Positive(message = "Order item ID must be positive")
        Long orderItemId,

        @NotNull(message = "Actual quantity is required")
        @DecimalMin(
                value = "0.001",
                message = "Actual quantity must be at least 0.001 KG"
        )
        BigDecimal actualQuantity
) {
}
