package com.bhusadukan.dto;

import com.bhusadukan.entity.Payment;
import jakarta.validation.constraints.NotNull;

public record UpdatePaymentStatusRequest(

        @NotNull(message = "Payment status is required")
        Payment.PaymentStatus status

) {
}