package com.bhusadukan.dto;

import com.bhusadukan.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(

        Long id,

        Long orderId,

        String orderNumber,

        Payment.PaymentMethod paymentMethod,

        Payment.PaymentStatus paymentStatus,

        String transactionId,

        BigDecimal amount,

        LocalDateTime paidAt,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}