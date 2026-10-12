package com.bhusadukan.repository;

import com.bhusadukan.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByProductIdOrderByCreatedAtDesc(
            Long productId);

    List<InventoryTransaction> findByReferenceTypeAndReferenceId(
            String referenceType,
            Long referenceId);
}