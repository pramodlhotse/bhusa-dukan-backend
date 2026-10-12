package com.bhusadukan.service;

import com.bhusadukan.dto.CreateDeliveryRequest;
import com.bhusadukan.dto.DeliveryResponse;
import com.bhusadukan.dto.UpdateDeliveryStatusRequest;
import com.bhusadukan.dto.VerifyDeliveryOtpRequest;
import com.bhusadukan.entity.*;
import com.bhusadukan.repository.*;
import com.bhusadukan.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public DeliveryResponse updateDeliveryStatus(
            Long deliveryId,
            UpdateDeliveryStatusRequest request) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery not found: " + deliveryId));

        Delivery.DeliveryStatus currentStatus = delivery.getStatus();
        Delivery.DeliveryStatus newStatus = request.status();

        validateStatusTransition(currentStatus, newStatus);

        LocalDateTime now = LocalDateTime.now();

        delivery.setStatus(newStatus);

        if (request.failureReason() != null) {
            delivery.setFailureReason(request.failureReason());
        }

        if (request.remarks() != null) {
            delivery.setRemarks(request.remarks());
        }

        Order order = delivery.getOrder();

        switch (newStatus) {

            case OUT_FOR_DELIVERY -> {
                delivery.setDispatchedAt(now);

                String otp = String.format(
                        "%06d",
                        secureRandom.nextInt(1_000_000)
                );

                delivery.setDeliveryOtp(otp);

                if (order.getStatus() == Order.OrderStatus.PACKED) {

                    Order.OrderStatus oldStatus = order.getStatus();

                    order.setStatus(Order.OrderStatus.OUT_FOR_DELIVERY);

                    orderStatusHistoryRepository.save(
                            OrderStatusHistory.builder()
                                    .order(order)
                                    .oldStatus(oldStatus)
                                    .newStatus(Order.OrderStatus.OUT_FOR_DELIVERY)
                                    .changedBy(delivery.getDeliveryPerson())
                                    .remarks("Order dispatched for delivery")
                                    .build()
                    );
                }
            }

            case DELIVERED -> {
                delivery.setDeliveredAt(now);
                delivery.setCustomerConfirmed(true);

                if (order.getStatus() == Order.OrderStatus.OUT_FOR_DELIVERY) {
                    order.setStatus(Order.OrderStatus.DELIVERED);
                    order.setDeliveredAt(now);
                }
            }

            case FAILED -> {
                delivery.setFailedAt(now);
            }

            case CANCELLED -> {
                // No additional timestamp required.
            }

            case ASSIGNED -> {
                // No additional action.
            }
        }

        Delivery savedDelivery = deliveryRepository.save(delivery);

        return toResponse(savedDelivery);
    }
    private void validateStatusTransition(
            Delivery.DeliveryStatus currentStatus,
            Delivery.DeliveryStatus newStatus) {

        if (currentStatus == newStatus) {
            throw new IllegalArgumentException(
                    "Delivery is already in status: " + currentStatus);
        }

        boolean valid = switch (currentStatus) {

            case ASSIGNED ->
                    newStatus == Delivery.DeliveryStatus.OUT_FOR_DELIVERY
                            || newStatus == Delivery.DeliveryStatus.CANCELLED;

            case OUT_FOR_DELIVERY ->
                    newStatus == Delivery.DeliveryStatus.DELIVERED
                            || newStatus == Delivery.DeliveryStatus.FAILED;

            case FAILED ->
                    newStatus == Delivery.DeliveryStatus.OUT_FOR_DELIVERY
                            || newStatus == Delivery.DeliveryStatus.CANCELLED;

            case DELIVERED, CANCELLED ->
                    false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid delivery status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus);
        }
    }

    @Transactional
    public DeliveryResponse createDelivery(CreateDeliveryRequest request) {

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + request.orderId()
                        ));

        if (order.getStatus() != Order.OrderStatus.PACKED) {
            throw new IllegalArgumentException(
                    "Delivery can be created only for PACKED orders"
            );
        }

        if (deliveryRepository.existsByOrderId(order.getId())) {
            throw new IllegalArgumentException(
                    "Delivery already exists for order: " + order.getId()
            );
        }

        User deliveryPerson = userRepository.findById(
                        request.deliveryPersonId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery person not found: "
                                        + request.deliveryPersonId()
                        ));

        if (!Boolean.TRUE.equals(deliveryPerson.getActive())) {
            throw new IllegalArgumentException(
                    "Delivery person is inactive"
            );
        }

        LocalDateTime now = LocalDateTime.now();

        Delivery delivery = Delivery.builder()
                .order(order)
                .deliveryPerson(deliveryPerson)
                .vehicleNumber(request.vehicleNumber())
                .status(Delivery.DeliveryStatus.ASSIGNED)
                .assignedAt(now)
                .customerConfirmed(false)
                .remarks(request.remarks())
                .build();

        Delivery savedDelivery =
                deliveryRepository.save(delivery);

        return toResponse(savedDelivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDelivery(Long deliveryId) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery not found: " + deliveryId
                        ));

        return toResponse(delivery);
    }

    @Transactional(readOnly = true)
    public DeliveryResponse getDeliveryByOrder(Long orderId) {

        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery not found for order: " + orderId
                        ));

        return toResponse(delivery);
    }

    @Transactional(readOnly = true)
    public List<DeliveryResponse> getDeliveriesByStatus(
            Delivery.DeliveryStatus status) {

        return deliveryRepository
                .findByStatusOrderByCreatedAtAsc(status)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private DeliveryResponse toResponse(Delivery delivery) {

        Order order = delivery.getOrder();
        User deliveryPerson = delivery.getDeliveryPerson();

        return new DeliveryResponse(
                delivery.getId(),
                order.getId(),
                order.getOrderNumber(),
                deliveryPerson != null
                        ? deliveryPerson.getId()
                        : null,
                deliveryPerson != null
                        ? deliveryPerson.getName()
                        : null,
                delivery.getVehicleNumber(),
                delivery.getStatus().name(),
                delivery.getAssignedAt(),
                delivery.getDispatchedAt(),
                delivery.getDeliveredAt(),
                delivery.getFailedAt(),
                delivery.getFailureReason(),
                delivery.getCustomerConfirmed(),
                delivery.getRemarks(),
                delivery.getCreatedAt(),
                delivery.getUpdatedAt()
        );
    }
    @Transactional
    public DeliveryResponse verifyDeliveryOtp(
            Long deliveryId,
            VerifyDeliveryOtpRequest request) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Delivery not found: " + deliveryId));

        if (delivery.getStatus()
                != Delivery.DeliveryStatus.OUT_FOR_DELIVERY) {

            throw new IllegalArgumentException(
                    "OTP can be verified only for OUT_FOR_DELIVERY deliveries");
        }

        if (delivery.getDeliveryOtp() == null) {
            throw new IllegalArgumentException(
                    "Delivery OTP has not been generated");
        }
        Payment.PaymentMethod paymentMethod = null;
        Payment.PaymentStatus paymentStatus = null;

        List<Payment> payments =
                paymentRepository.findByOrderIdOrderByCreatedAtDesc(
                        delivery.getOrder().getId());

        if (!payments.isEmpty()) {
            Payment latestPayment = payments.get(0);

            paymentMethod = latestPayment.getPaymentMethod();
            paymentStatus = latestPayment.getPaymentStatus();
        }

        if (paymentMethod == Payment.PaymentMethod.CASH
                && paymentStatus != Payment.PaymentStatus.SUCCESS) {

            throw new IllegalArgumentException(
                    "COD payment must be successful before delivery confirmation");
        }

        if (!delivery.getDeliveryOtp().equals(request.otp())) {
            throw new IllegalArgumentException("Invalid delivery OTP");
        }

        LocalDateTime now = LocalDateTime.now();

        delivery.setStatus(Delivery.DeliveryStatus.DELIVERED);
        delivery.setDeliveredAt(now);
        delivery.setCustomerConfirmed(true);

        // Clear OTP after successful verification
        delivery.setDeliveryOtp(null);

        Order order = delivery.getOrder();

        if (order.getStatus() == Order.OrderStatus.OUT_FOR_DELIVERY) {
            order.setStatus(Order.OrderStatus.DELIVERED);
            order.setDeliveredAt(now);
        }

        Delivery savedDelivery = deliveryRepository.save(delivery);

        return toResponse(savedDelivery);
    }

}