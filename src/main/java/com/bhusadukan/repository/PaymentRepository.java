package com.bhusadukan.repository;

import com.bhusadukan.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderIdOrderByCreatedAtDesc(Long orderId);

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByOrderIdAndPaymentStatus(
            Long orderId,
            Payment.PaymentStatus paymentStatus
    );
}