package com.bhusadukan.dto;

import com.bhusadukan.entity.Payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        Long customerId,
        Long deliveryAddressId,
        String status,
        BigDecimal subtotal,
        BigDecimal deliveryCharge,
        BigDecimal discountAmount,
        BigDecimal finalAmount,
        String customerNotes,
        LocalDateTime orderedAt,

        Payment.PaymentMethod paymentMethod,
        Payment.PaymentStatus paymentStatus,

        List<OrderItemResponse> items
) {
}