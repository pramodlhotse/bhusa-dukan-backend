package com.bhusadukan.repository;

import com.bhusadukan.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository
        extends JpaRepository<Delivery, Long> {

    Optional<Delivery> findByOrderId(Long orderId);

    List<Delivery> findByDeliveryPersonIdOrderByCreatedAtDesc(
            Long deliveryPersonId);

    List<Delivery> findByStatusOrderByCreatedAtAsc(
            Delivery.DeliveryStatus status);

    boolean existsByOrderId(Long orderId);
}