package com.bhusadukan.service;

import com.bhusadukan.dto.CreateWeighingRequest;
import com.bhusadukan.dto.WeighingResponse;
import com.bhusadukan.entity.*;
import com.bhusadukan.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WeighingService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final WeighingRecordRepository weighingRecordRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public WeighingResponse createWeighing(
            Long orderItemId,
            CreateWeighingRequest request) {

        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order item not found: " + orderItemId));

        Order order = orderItem.getOrder();

        if (order.getStatus() != Order.OrderStatus.WEIGHING) {
            throw new IllegalArgumentException(
                    "Order must be in WEIGHING status");
        }

        if (weighingRecordRepository
                .findByOrderItemId(orderItemId)
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Weighing already exists for order item: "
                            + orderItemId);
        }

        if (request.actualWeight() == null) {
            throw new IllegalArgumentException(
                    "Actual weight is required");
        }

        BigDecimal orderedWeight = orderItem.getOrderedQuantity();
        BigDecimal actualWeight = request.actualWeight();

        if (actualWeight.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Actual weight must be greater than zero");
        }

        /*
         * Lock inventory row so concurrent weighing operations
         * cannot modify the same stock at the same time.
         */
        Inventory inventory = inventoryRepository
                .findWithLockByProductId(
                        orderItem.getProduct().getId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Inventory not found for product: "
                                        + orderItem.getProduct().getId()));

        BigDecimal reservedQuantity =
                inventory.getReservedQuantity();

        if (reservedQuantity.compareTo(orderedWeight) < 0) {
            throw new IllegalArgumentException(
                    "Reserved quantity is less than ordered quantity");
        }

        /*
         * If actual weight is greater than ordered weight,
         * additional stock must be available.
         */
        BigDecimal difference =
                actualWeight.subtract(orderedWeight);

        if (difference.compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal availableQuantity =
                    inventory.getCurrentQuantity()
                            .subtract(inventory.getReservedQuantity());

            if (availableQuantity.compareTo(difference) < 0) {
                throw new IllegalArgumentException(
                        "Insufficient inventory for additional actual weight");
            }
        }

        /*
         * The ordered quantity was already reserved during
         * order creation. Release that reservation.
         */
        inventory.setReservedQuantity(
                inventory.getReservedQuantity()
                        .subtract(orderedWeight)
        );

        /*
         * Actual physical quantity is now consumed from inventory.
         */
        inventory.setCurrentQuantity(
                inventory.getCurrentQuantity()
                        .subtract(actualWeight)
        );

        /*
         * Update order item with actual weight.
         */
        orderItem.setActualQuantity(actualWeight);

        BigDecimal finalAmount =
                actualWeight.multiply(orderItem.getUnitPrice());

        orderItem.setFinalAmount(finalAmount);

        /*
         * Recalculate order totals.
         *
         * We use final amount for weighed items and estimated
         * amount for items that are not weighed yet.
         */
        BigDecimal orderTotal = order.getItems()
                .stream()
                .map(item -> item.getFinalAmount() != null
                        ? item.getFinalAmount()
                        : item.getEstimatedAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setSubtotal(orderTotal);

        BigDecimal finalOrderAmount = orderTotal
                .add(order.getDeliveryCharge())
                .subtract(order.getDiscountAmount());

        order.setFinalAmount(finalOrderAmount);

        /*
         * Create weighing record.
         */
        User verifiedBy = null;

        if (request.verifiedBy() != null) {
            verifiedBy = userRepository.findById(request.verifiedBy())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "User not found: "
                                            + request.verifiedBy()));
        }

        LocalDateTime now = LocalDateTime.now();

        WeighingRecord weighingRecord = WeighingRecord.builder()
                .orderItem(orderItem)
                .orderedWeight(orderedWeight)
                .actualWeight(actualWeight)
                .weighingMachineId(request.weighingMachineId())
                .weighingMachineReading(
                        request.weighingMachineReading())
                .verifiedBy(verifiedBy)
                .verifiedAt(now)
                .remarks(request.remarks())
                .build();

        WeighingRecord savedRecord =
                weighingRecordRepository.save(weighingRecord);

        orderItemRepository.save(orderItem);
        inventoryRepository.save(inventory);
        InventoryTransaction inventoryTransaction =
                InventoryTransaction.builder()
                        .product(orderItem.getProduct())
                        .transactionType(
                                InventoryTransaction.TransactionType.SALE)
                        .quantity(actualWeight)
                        .referenceType("ORDER")
                        .referenceId(order.getId())
                        .remarks(
                                "Bhusa sold against order "
                                        + order.getOrderNumber())
                        .createdBy(verifiedBy)
                        .build();

        inventoryTransactionRepository.save(inventoryTransaction);
        orderRepository.save(order);

        return toResponse(savedRecord);
    }

    private WeighingResponse toResponse(
            WeighingRecord record) {

        return new WeighingResponse(
                record.getId(),
                record.getOrderItem().getOrder().getId(),
                record.getOrderItem().getId(),
                record.getOrderedWeight(),
                record.getActualWeight(),
                record.getWeighingMachineReading(),
                record.getWeighingMachineId(),
                record.getVerifiedBy() != null
                        ? record.getVerifiedBy().getId()
                        : null,
                record.getVerifiedAt(),
                record.getRemarks(),
                record.getCreatedAt()
        );
    }
}