package com.bhusadukan.service;

import com.bhusadukan.dto.ProductResponse;
import com.bhusadukan.entity.Product;
import com.bhusadukan.entity.ProductPrice;
import com.bhusadukan.repository.InventoryRepository;
import com.bhusadukan.repository.ProductPriceRepository;
import com.bhusadukan.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductPriceRepository productPriceRepository;
    private final InventoryRepository inventoryRepository;

    public List<ProductResponse> getActiveProducts() {

        LocalDateTime now = LocalDateTime.now();

        return productRepository.findByActiveTrue()
                .stream()
                .map(product -> toResponse(product, now))
                .toList();
    }

    public ProductResponse getProduct(Long productId) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + productId));

        if (!Boolean.TRUE.equals(product.getActive())) {
            throw new RuntimeException("Product is not active: " + productId);
        }

        return toResponse(product, LocalDateTime.now());
    }

    private ProductResponse toResponse(
            Product product,
            LocalDateTime now) {

        BigDecimal price = productPriceRepository
                .findFirstByProductIdAndActiveTrueAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
                        product.getId(),
                        now
                )
                .map(ProductPrice::getPrice)
                .orElse(BigDecimal.ZERO);

        BigDecimal stock = inventoryRepository
                .findByProductId(product.getId())
                .map(inventory -> inventory.getCurrentQuantity()
                        .subtract(inventory.getReservedQuantity()))
                .orElse(BigDecimal.ZERO);

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getUnit(),
                product.getMinimumQuantity(),
                price,
                stock,
                Boolean.TRUE.equals(product.getActive())
        );
    }
}