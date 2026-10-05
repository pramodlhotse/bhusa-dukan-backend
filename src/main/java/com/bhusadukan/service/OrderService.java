package com.bhusadukan.service;

import com.bhusadukan.dto.CreateOrderItemRequest;
import com.bhusadukan.dto.CreateOrderRequest;
import com.bhusadukan.dto.OrderItemResponse;
import com.bhusadukan.dto.OrderResponse;
import com.bhusadukan.entity.CustomerAddress;
import com.bhusadukan.entity.Inventory;
import com.bhusadukan.entity.Order;
import com.bhusadukan.entity.OrderItem;
import com.bhusadukan.entity.Product;
import com.bhusadukan.entity.ProductPrice;
import com.bhusadukan.entity.User;
import com.bhusadukan.repository.CustomerAddressRepository;
import com.bhusadukan.repository.InventoryRepository;
import com.bhusadukan.repository.OrderRepository;
import com.bhusadukan.repository.ProductPriceRepository;
import com.bhusadukan.repository.ProductRepository;
import com.bhusadukan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        // 1. Validate customer
        User customer = userRepository.findById(request.customerId())
                .filter(user -> user.getRole() == User.Role.CUSTOMER)
                .filter(User::getActive)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found: " + request.customerId()
                        )
                );

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

        // 15. Save order items
        // We save through EntityManager cascade later.
        // For now this is handled by OrderItemRepository in next refinement.

        return toResponse(savedOrder, orderItems);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId
                        )
                );

        return toResponse(order, List.of());
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
                itemResponses
        );
    }
}