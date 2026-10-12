package com.bhusadukan.service;

import com.bhusadukan.dto.CreatePaymentRequest;
import com.bhusadukan.dto.PaymentResponse;
import com.bhusadukan.dto.UpdatePaymentStatusRequest;
import com.bhusadukan.entity.Order;
import com.bhusadukan.entity.Payment;
import com.bhusadukan.repository.OrderRepository;
import com.bhusadukan.repository.PaymentRepository;
import com.bhusadukan.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final CurrentUser currentUser;

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {

        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + request.orderId()));
        boolean isStaffOrAdmin =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_ADMIN")
                                        || authority.getAuthority().equals("ROLE_STAFF"));

        if (!isStaffOrAdmin
                && !order.getCustomer().getId().equals(currentUser.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to create a payment for this order");
        }
        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Payment cannot be created for a cancelled order");
        }

        if (request.amount().compareTo(order.getFinalAmount()) != 0) {
            throw new IllegalArgumentException(
                    "Payment amount must match order final amount: "
                            + order.getFinalAmount());
        }

        if (request.paymentMethod() != Payment.PaymentMethod.CASH
                && (request.transactionId() == null
                || request.transactionId().isBlank())) {

            throw new IllegalArgumentException(
                    "Transaction ID is required for UPI and ONLINE payments");
        }

        if (request.transactionId() != null
                && !request.transactionId().isBlank()
                && paymentRepository
                .findByTransactionId(request.transactionId())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Payment transaction already exists: "
                            + request.transactionId());
        }

        boolean successfulPaymentExists =
                paymentRepository
                        .findByOrderIdAndPaymentStatus(
                                order.getId(),
                                Payment.PaymentStatus.SUCCESS)
                        .isPresent();

        if (successfulPaymentExists) {
            throw new IllegalArgumentException(
                    "Order already has a successful payment");
        }

        Payment payment = Payment.builder()
                .order(order)
                .paymentMethod(request.paymentMethod())
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .transactionId(request.transactionId())
                .amount(request.amount())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(Long paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment not found: " + paymentId));

        boolean isStaffOrAdmin =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_ADMIN")
                                        || authority.getAuthority().equals("ROLE_STAFF"));

        if (!isStaffOrAdmin
                && !payment.getOrder().getCustomer().getId()
                .equals(currentUser.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to view this payment");
        }

        return toResponse(payment);
    }


    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Order not found: " + orderId));

        boolean isStaffOrAdmin =
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_ADMIN")
                                        || authority.getAuthority().equals("ROLE_STAFF"));

        if (!isStaffOrAdmin
                && !order.getCustomer().getId()
                .equals(currentUser.getUserId())) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "You are not allowed to view payments for this order");
        }

        return paymentRepository
                .findByOrderIdOrderByCreatedAtDesc(orderId)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    private PaymentResponse toResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getOrder().getOrderNumber(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getTransactionId(),
                payment.getAmount(),
                payment.getPaidAt(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
    @Transactional
    public PaymentResponse updatePaymentStatus(
            Long paymentId,
            UpdatePaymentStatusRequest request) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment not found: " + paymentId));

        Payment.PaymentStatus currentStatus =
                payment.getPaymentStatus();

        Payment.PaymentStatus newStatus =
                request.status();

        validateStatusTransition(currentStatus, newStatus);

        LocalDateTime now = LocalDateTime.now();

        if (newStatus == Payment.PaymentStatus.SUCCESS) {

            payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
            payment.setPaidAt(now);

        } else if (newStatus == Payment.PaymentStatus.FAILED) {

            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);

        } else if (newStatus == Payment.PaymentStatus.REFUNDED) {

            payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);

        } else {
            payment.setPaymentStatus(newStatus);
        }

        Payment savedPayment = paymentRepository.save(payment);

        return toResponse(savedPayment);
    }
    private void validateStatusTransition(
            Payment.PaymentStatus currentStatus,
            Payment.PaymentStatus newStatus) {

        if (currentStatus == newStatus) {
            throw new IllegalArgumentException(
                    "Payment is already in status: " + currentStatus);
        }

        boolean valid = switch (currentStatus) {

            case PENDING ->
                    newStatus == Payment.PaymentStatus.SUCCESS
                            || newStatus == Payment.PaymentStatus.FAILED;

            case SUCCESS ->
                    newStatus == Payment.PaymentStatus.REFUNDED;

            case FAILED ->
                    newStatus == Payment.PaymentStatus.PENDING;

            case REFUNDED ->
                    false;
        };

        if (!valid) {
            throw new IllegalArgumentException(
                    "Invalid payment status transition: "
                            + currentStatus
                            + " -> "
                            + newStatus);
        }
    }
}