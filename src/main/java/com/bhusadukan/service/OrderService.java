package com.bhusadukan.service;

import com.bhusadukan.dto.*;
import com.bhusadukan.entity.*;
import com.bhusadukan.repository.*;
import com.bhusadukan.security.CurrentUser;
import com.bhusadukan.dto.UpdateActualWeightRequest;
import com.bhusadukan.entity.OrderItem;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Transactional;
import com.bhusadukan.entity.Payment;
import com.bhusadukan.repository.PaymentRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final BigDecimal DEFAULT_DELIVERY_CHARGE =
            BigDecimal.ZERO;

    private final UserRepository userRepository;
    private final CustomerAddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final PaymentRepository paymentRepository;
    private final CurrentUser currentUser;
    private final OrderItemRepository orderItemRepository;


    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        // Get the customer ID from the authenticated JWT.
        Long authenticatedUserId = currentUser.getUserId();

        // Validate the authenticated user as an active customer.
        User customer = userRepository.findById(authenticatedUserId)
                .filter(user -> user.getRole() == User.Role.CUSTOMER)
                .filter(user -> Boolean.TRUE.equals(user.getActive()))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Active customer account not found"));

        // Continue with the existing delivery-address validation,
        // inventory reservation, pricing and order creation logic.

        // 2. Validate delivery address
        CustomerAddress address =
                addressRepository.findByIdAndUserId(
                                request.deliveryAddressId(),
                                customer.getId()
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Delivery address not found for customer"
                                )
                        );

        // 3. Create order
        Order order = Order.builder()
                .orderNumber(generateOrderNumber())
                .customer(customer)
                .deliveryAddress(address)
                .status(Order.OrderStatus.PLACED)
                .subtotal(BigDecimal.ZERO)
                .deliveryCharge(DEFAULT_DELIVERY_CHARGE)
                .discountAmount(BigDecimal.ZERO)
                .finalAmount(BigDecimal.ZERO)
                .customerNotes(request.customerNotes())
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        List<OrderItem> orderItems = new ArrayList<>();

        // 4. Process each product
        for (CreateOrderItemRequest itemRequest : request.items()) {

            Product product = productRepository
                    .findById(itemRequest.productId())
                    .filter(Product::getActive)
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Product not found: "
                                            + itemRequest.productId()
                            )
                    );

            BigDecimal requestedQuantity = itemRequest.quantity();

            // 5. Validate minimum quantity
            if (requestedQuantity.compareTo(
                    product.getMinimumQuantity()) < 0) {

                throw new IllegalArgumentException(
                        "Minimum quantity for "
                                + product.getName()
                                + " is "
                                + product.getMinimumQuantity()
                                + " "
                                + product.getUnit()
                );
            }

            // 6. Get current price
            LocalDateTime now = LocalDateTime.now();

            BigDecimal unitPrice =
                    productPriceRepository
                            .findFirstByProductIdAndActiveTrueAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                                    product.getId(),
                                    now
                            )
                            .map(ProductPrice::getPrice)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "No active price found for "
                                                    + product.getName()
                                    )
                            );

            // 7. Lock inventory row
            Inventory inventory =
                    inventoryRepository
                            .findWithLockByProductId(product.getId())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Inventory not found for "
                                                    + product.getName()
                                    )
                            );

            BigDecimal availableStock =
                    inventory.getCurrentQuantity()
                            .subtract(inventory.getReservedQuantity());

            // 8. Check stock
            if (availableStock.compareTo(requestedQuantity) < 0) {

                throw new IllegalArgumentException(
                        "Insufficient stock for "
                                + product.getName()
                                + ". Available: "
                                + availableStock
                                + " "
                                + product.getUnit()
                );
            }

            // 9. Calculate estimated amount
            BigDecimal estimatedAmount =
                    unitPrice
                            .multiply(requestedQuantity)
                            .setScale(2, RoundingMode.HALF_UP);

            // 10. Reserve stock
            inventory.setReservedQuantity(
                    inventory.getReservedQuantity()
                            .add(requestedQuantity)
            );

            inventoryRepository.save(inventory);

            // 11. Create order item
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .orderedQuantity(requestedQuantity)
                    .unitPrice(unitPrice)
                    .estimatedAmount(estimatedAmount)
                    .build();

            orderItems.add(orderItem);

            subtotal = subtotal.add(estimatedAmount);
        }

        // 12. Calculate final amount
        BigDecimal finalAmount =
                subtotal
                        .add(DEFAULT_DELIVERY_CHARGE)
                        .subtract(BigDecimal.ZERO)
                        .setScale(2, RoundingMode.HALF_UP);

        order.setSubtotal(subtotal);
        order.setFinalAmount(finalAmount);

        // 13. Connect items to order
        orderItems.forEach(orderItem -> orderItem.setOrder(order));
        order.setItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        Payment payment = Payment.builder()
                .order(savedOrder)
                .paymentMethod(request.paymentMethod())
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .amount(savedOrder.getFinalAmount())
                .build();

        paymentRepository.save(payment);

        OrderStatusHistory initialHistory = OrderStatusHistory.builder()
                .order(savedOrder)
                .oldStatus(null)
                .newStatus(Order.OrderStatus.PLACED)
                .changedBy(null)
                .remarks("Order placed")
                .build();

        orderStatusHistoryRepository.save(initialHistory);

        return toResponse(savedOrder, orderItems);

        // 15. Save order items
        // We save through EntityManager cascade later.
        // For now this is handled by OrderItemRepository in next refinement.

    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getCustomerOrders(Long customerId) {

        Long authenticatedUserId = currentUser.getUserId();

        if (!authenticatedUserId.equals(customerId)) {
            User.Role role = userRepository.findById(authenticatedUserId)
                    .map(User::getRole)
                    .orElseThrow(() ->
                            new org.springframework.security.access.AccessDeniedException(
                                    "User account not found"));

            if (role != User.Role.ADMIN && role != User.Role.STAFF) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "You cannot access another customer's orders");
            }
        }

        User customer = userRepository.findById(customerId)
                .filter(user -> user.getRole() == User.Role.CUSTOMER)
                .filter(user -> Boolean.TRUE.equals(user.getActive()))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found: " + customerId));

        return orderRepository
                .findByCustomerIdOrderByOrderedAtDesc(customer.getId())
                .stream()
                .map(order -> toResponse(order, order.getItems()))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        Order order = getOrderForCurrentUser(orderId);
        return toResponse(order, order.getItems());
    }

    private String generateOrderNumber() {

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));

        return "BD-" + timestamp;
    }

    private OrderResponse toResponse(
            Order order,
            List<OrderItem> items) {

        List<OrderItemResponse> itemResponses =
                items.stream()
                        .map(item -> new OrderItemResponse(
                                item.getId(),
                                item.getProduct().getId(),
                                item.getProduct().getName(),
                                item.getOrderedQuantity(),
                                item.getActualQuantity(),
                                item.getUnitPrice(),
                                item.getEstimatedAmount(),
                                item.getFinalAmount()
                        ))
                        .toList();

        Payment.PaymentMethod paymentMethod = null;
        Payment.PaymentStatus paymentStatus = null;

        List<Payment> payments =
                paymentRepository.findByOrderIdOrderByCreatedAtDesc(
                        order.getId());

        if (!payments.isEmpty()) {
            Payment latestPayment = payments.get(0);

            paymentMethod = latestPayment.getPaymentMethod();
            paymentStatus = latestPayment.getPaymentStatus();
        }

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCustomer().getId(),
                order.getDeliveryAddress() != null
                        ? order.getDeliveryAddress().getId()
                        : null,
                order.getStatus().name(),
                order.getSubtotal(),
                order.getDeliveryCharge(),
                order.getDiscountAmount(),
                order.getFinalAmount(),
                order.getCustomerNotes(),
                order.getOrderedAt(),
                paymentMethod,
                paymentStatus,
                itemResponses
        );
    }
    @Transactional
    public OrderResponse updateOrderStatus(
            Long orderId,
            UpdateOrderStatusRequest request) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId));

        Order.OrderStatus oldStatus = order.getStatus();
        Order.OrderStatus newStatus = request.newStatus();

        validateStatusTransition(oldStatus, newStatus);

        order.setStatus(newStatus);

        LocalDateTime now = LocalDateTime.now();

        switch (newStatus) {
            case CONFIRMED -> order.setConfirmedAt(now);
            case DELIVERED -> order.setDeliveredAt(now);
            case CANCELLED -> order.setCancelledAt(now);
        }

        if (newStatus == Order.OrderStatus.CANCELLED) {
            order.setCancellationReason(request.remarks());
        }

        OrderStatusHistory history = OrderStatusHistory.builder()
                .order(order)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(
                        request.changedBy() == null
                                ? null
                                : userRepository.findById(request.changedBy())
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "User not found: "
                                                        + request.changedBy()))
                )
                .remarks(request.remarks())
                .build();

        orderStatusHistoryRepository.save(history);

        Order savedOrder = orderRepository.save(order);

        return toResponse(
                savedOrder,
                savedOrder.getItems()
        );
    }
    private void validateStatusTransition(
            Order.OrderStatus oldStatus,
            Order.OrderStatus newStatus) {

        if (oldStatus == newStatus) {
            throw new IllegalArgumentException(
                    "Order is already in status: " + newStatus);
        }

        if (oldStatus == Order.OrderStatus.CANCELLED
                || oldStatus == Order.OrderStatus.DELIVERED) {

            throw new IllegalArgumentException(
                    "Order cannot be changed from " + oldStatus);
        }

        boolean valid = switch (oldStatus) {

            case PLACED ->
                    newStatus == Order.OrderStatus.CONFIRMED
                            || newStatus == Order.OrderStatus.CANCELLED;

            case CONFIRMED ->
                    newStatus == Order.OrderStatus.WEIGHING
                            || newStatus == Order.OrderStatus.CANCELLED;

            case WEIGHING ->
                    newStatus == Order.OrderStatus.PACKED
                            || newStatus == Order.OrderStatus.CANCELLED;

            case PACKED ->
                    newStatus == Order.OrderStatus.OUT_FOR_DELIVERY
                            || newStatus == Order.OrderStatus.CANCELLED;

            case OUT_FOR_DELIVERY ->
                    newStatus == Order.OrderStatus.DELIVERED;

            case DELIVERED, CANCELLED ->
                    false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid order status transition: "
                            + oldStatus + " -> " + newStatus);
        }
    }
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponse> getOrderStatusHistory(
            Long orderId) {

        getOrderForCurrentUser(orderId);

        return orderStatusHistoryRepository
                .findByOrderIdOrderByCreatedAtAsc(orderId)
                .stream()
                .map(history -> new OrderStatusHistoryResponse(
                        history.getId(),
                        history.getOldStatus() == null
                                ? null
                                : history.getOldStatus().name(),
                        history.getNewStatus().name(),
                        history.getChangedBy() == null
                                ? null
                                : history.getChangedBy().getId(),
                        history.getRemarks(),
                        history.getCreatedAt()
                ))
                .toList();
    }

    private Order getOrderForCurrentUser(Long orderId) {
        Long authenticatedUserId = currentUser.getUserId();

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId));

        User.Role role = userRepository.findById(authenticatedUserId)
                .map(User::getRole)
                .orElseThrow(() ->
                        new AccessDeniedException("User account not found"));

        boolean isStaffOrAdmin =
                role == User.Role.ADMIN || role == User.Role.STAFF;

        boolean isOwner = order.getCustomer() != null
                && authenticatedUserId.equals(
                order.getCustomer().getId());

        if (!isOwner && !isStaffOrAdmin) {
            throw new AccessDeniedException(
                    "You are not authorized to access this order");
        }

        return order;
    }

    @Transactional
    public OrderResponse updateActualWeight(
            Long orderId,
            UpdateActualWeightRequest request) {

        Long authenticatedUserId = currentUser.getUserId();

        User.Role role = userRepository.findById(authenticatedUserId)
                .map(User::getRole)
                .orElseThrow(() ->
                        new AccessDeniedException("User account not found"));

        if (role != User.Role.ADMIN && role != User.Role.STAFF) {
            throw new AccessDeniedException(
                    "Only staff or admin can record actual weight");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId));

        if (order.getStatus() != Order.OrderStatus.WEIGHING) {
            throw new IllegalArgumentException(
                    "Actual weight can only be recorded when order is in WEIGHING status");
        }

        OrderItem item = orderItemRepository.findById(request.orderItemId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order item not found: " + request.orderItemId()));

        if (item.getOrder() == null
                || !order.getId().equals(item.getOrder().getId())) {
            throw new IllegalArgumentException(
                    "Order item does not belong to this order");
        }

        item.setActualQuantity(request.actualQuantity());

        orderItemRepository.save(item);

        Order savedOrder = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId));

        return toResponse(savedOrder, savedOrder.getItems());
    }

}