package com.bhusadukan.controller;

import com.bhusadukan.dto.CreateOrderRequest;
import com.bhusadukan.dto.OrderResponse;
import com.bhusadukan.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(
        name = "Orders",
        description = "APIs for creating and managing Bhusa orders"
)
public class OrderController {

    private final OrderService orderService;

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
    public OrderResponse getOrder(
            @PathVariable Long orderId) {

        return orderService.getOrder(orderId);
    }
}