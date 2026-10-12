package com.bhusadukan.dto;

import com.bhusadukan.entity.Payment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(

        @NotNull(message = "Delivery address ID is required")
        Long deliveryAddressId,

        @NotNull(message = "Payment method is required")
        Payment.PaymentMethod paymentMethod,

        @NotEmpty(message = "Order must contain at least one product")
        List<@Valid CreateOrderItemRequest> items,

        String customerNotes
) {
}
