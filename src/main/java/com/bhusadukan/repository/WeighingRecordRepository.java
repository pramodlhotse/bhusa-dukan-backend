package com.bhusadukan.repository;

import com.bhusadukan.entity.WeighingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeighingRecordRepository
        extends JpaRepository<WeighingRecord, Long> {

    List<WeighingRecord> findByOrderItemOrderIdOrderByCreatedAtAsc(
            Long orderId);

    Optional<WeighingRecord> findByOrderItemId(Long orderItemId);
}