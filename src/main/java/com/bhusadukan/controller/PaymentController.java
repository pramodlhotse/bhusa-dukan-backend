package com.bhusadukan.controller;

import com.bhusadukan.dto.CreatePaymentRequest;
import com.bhusadukan.dto.PaymentResponse;
import com.bhusadukan.dto.UpdatePaymentStatusRequest;
import com.bhusadukan.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(
        name = "Payments",
        description = "APIs for managing Bhusa order payments"
)
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Create payment",
            description = "Creates a pending payment for an order"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse createPayment(
            @Valid @RequestBody CreatePaymentRequest request) {

        return paymentService.createPayment(request);
    }

    @Operation(
            summary = "Get payment",
            description = "Returns payment details by payment ID"
    )
    @GetMapping("/{paymentId}")
    public PaymentResponse getPayment(
            @PathVariable Long paymentId) {

        return paymentService.getPayment(paymentId);
    }

    @Operation(
            summary = "Get payments by order",
            description = "Returns payment history for an order"
    )
    @GetMapping("/order/{orderId}")
    public List<PaymentResponse> getPaymentsByOrder(
            @PathVariable Long orderId) {

        return paymentService.getPaymentsByOrder(orderId);
    }
    @Operation(
            summary = "Update payment status",
            description = "Updates payment status and records the payment timestamp when successful"
    )
    @PutMapping("/{paymentId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PaymentResponse updatePaymentStatus(
            @PathVariable Long paymentId,
            @Valid @RequestBody UpdatePaymentStatusRequest request) {

        return paymentService.updatePaymentStatus(paymentId, request);
    }
}