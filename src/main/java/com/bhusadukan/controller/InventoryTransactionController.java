package com.bhusadukan.controller;

import com.bhusadukan.dto.InventoryTransactionResponse;
import com.bhusadukan.service.InventoryTransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory/transactions")
@RequiredArgsConstructor
@Tag(
        name = "Inventory Transactions",
        description = "APIs for viewing inventory transaction history"
)
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    @Operation(
            summary = "Get product inventory transactions",
            description = "Returns inventory transaction history for a product"
    )
    @GetMapping("/product/{productId}")
    public List<InventoryTransactionResponse> getProductTransactions(
            @PathVariable Long productId) {

        return inventoryTransactionService
                .getProductTransactions(productId);
    }

    @Operation(
            summary = "Get reference inventory transactions",
            description = "Returns inventory transactions for a reference such as an order"
    )
    @GetMapping("/reference/{referenceType}/{referenceId}")
    public List<InventoryTransactionResponse> getReferenceTransactions(
            @PathVariable String referenceType,
            @PathVariable Long referenceId) {

        return inventoryTransactionService
                .getReferenceTransactions(
                        referenceType,
                        referenceId
                );
    }
}