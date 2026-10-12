package com.bhusadukan.service;

import com.bhusadukan.dto.InventoryTransactionResponse;
import com.bhusadukan.entity.InventoryTransaction;
import com.bhusadukan.repository.InventoryTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;

    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getProductTransactions(
            Long productId) {

        return inventoryTransactionRepository
                .findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryTransactionResponse> getReferenceTransactions(
            String referenceType,
            Long referenceId) {

        return inventoryTransactionRepository
                .findByReferenceTypeAndReferenceId(
                        referenceType,
                        referenceId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private InventoryTransactionResponse toResponse(
            InventoryTransaction transaction) {

        return new InventoryTransactionResponse(
                transaction.getId(),
                transaction.getProduct().getId(),
                transaction.getProduct().getName(),
                transaction.getTransactionType().name(),
                transaction.getQuantity(),
                transaction.getReferenceType(),
                transaction.getReferenceId(),
                transaction.getRemarks(),
                transaction.getCreatedBy() != null
                        ? transaction.getCreatedBy().getId()
                        : null,
                transaction.getCreatedAt()
        );
    }
}