package com.bhusadukan.controller;

import com.bhusadukan.dto.CreateDeliveryRequest;
import com.bhusadukan.dto.DeliveryResponse;
import com.bhusadukan.dto.UpdateDeliveryStatusRequest;
import com.bhusadukan.dto.VerifyDeliveryOtpRequest;
import com.bhusadukan.entity.Delivery;
import com.bhusadukan.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@Tag(
        name = "Deliveries",
        description = "APIs for managing Bhusa order deliveries"
)
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(
            summary = "Create delivery",
            description = "Creates a delivery assignment for a packed order"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeliveryResponse createDelivery(
            @Valid @RequestBody CreateDeliveryRequest request) {

        return deliveryService.createDelivery(request);
    }

    @Operation(
            summary = "Get delivery",
            description = "Returns delivery details by delivery ID"
    )
    @GetMapping("/{deliveryId}")
    public DeliveryResponse getDelivery(
            @PathVariable Long deliveryId) {

        return deliveryService.getDelivery(deliveryId);
    }

    @Operation(
            summary = "Get delivery by order",
            description = "Returns delivery details for an order"
    )
    @GetMapping("/order/{orderId}")
    public DeliveryResponse getDeliveryByOrder(
            @PathVariable Long orderId) {

        return deliveryService.getDeliveryByOrder(orderId);
    }

    @Operation(
            summary = "Get deliveries by status",
            description = "Returns deliveries filtered by delivery status"
    )
    @GetMapping("/status/{status}")
    public List<DeliveryResponse> getDeliveriesByStatus(
            @Parameter(
                    description = "Delivery status"
            )
            @PathVariable Delivery.DeliveryStatus status) {

        return deliveryService.getDeliveriesByStatus(status);
    }

    @Operation(
            summary = "Update delivery status",
            description = "Updates delivery status and synchronizes the related order status"
    )
    @PutMapping("/{deliveryId}/status")
    public DeliveryResponse updateDeliveryStatus(
            @PathVariable Long deliveryId,
            @Valid @RequestBody UpdateDeliveryStatusRequest request) {

        return deliveryService.updateDeliveryStatus(deliveryId, request);
    }
    @Operation(
            summary = "Verify delivery OTP",
            description = "Verifies the customer OTP and marks the delivery and order as delivered"
    )
    @PostMapping("/{deliveryId}/verify-otp")
    public DeliveryResponse verifyDeliveryOtp(
            @PathVariable Long deliveryId,
            @Valid @RequestBody VerifyDeliveryOtpRequest request) {

        return deliveryService.verifyDeliveryOtp(deliveryId, request);
    }
}