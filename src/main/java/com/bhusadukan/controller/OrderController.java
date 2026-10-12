 package com.bhusadukan.controller;

import com.bhusadukan.dto.*;
import com.bhusadukan.security.CurrentUser;
import com.bhusadukan.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(
        name = "Orders",
        description = "APIs for creating and managing Bhusa orders"
)
public class OrderController {

    private final OrderService orderService;
    private final CurrentUser currentUser;

    @Operation(
            summary = "Create order",
            description = "Creates a new Bhusa order and reserves the requested stock"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        return orderService.createOrder(request);
    }

    @Operation(
            summary = "Get order",
            description = "Returns order details including order items"
    )
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    @Operation(
            summary = "Update order status",
            description = "Updates the order status and records the status history"
    )
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public OrderResponse updateOrderStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {

        return orderService.updateOrderStatus(orderId, request);
    }

    @Operation(
            summary = "Get order status history",
            description = "Returns the complete status change history of an order"
    )
    @GetMapping("/{orderId}/status-history")
    public List<OrderStatusHistoryResponse> getOrderStatusHistory(
            @PathVariable Long orderId) {

        return orderService.getOrderStatusHistory(orderId);
    }

    @GetMapping("/customer/{customerId}")
    public List<OrderResponse> getCustomerOrders(
            @PathVariable Long customerId) {

        Long authenticatedUserId = currentUser.getUserId();

        if (!authenticatedUserId.equals(customerId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You cannot access another customer's orders");
        }

        return orderService.getCustomerOrders(authenticatedUserId);
    }

    @Operation(
            summary = "Record actual order-item weight",
            description = "Records the actual weighed quantity for an order item"
    )
    @PutMapping("/{orderId}/items/actual-weight")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public OrderResponse updateActualWeight(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateActualWeightRequest request) {

        return orderService.updateActualWeight(orderId, request);
    }

}
