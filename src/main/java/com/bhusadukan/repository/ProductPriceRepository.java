package com.bhusadukan.repository;

import com.bhusadukan.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ProductPriceRepository
        extends JpaRepository<ProductPrice, Long> {

    Optional<ProductPrice> findFirstByProductIdAndActiveTrueAndEffectiveFromLessThanEqualOrderByEffectiveFromDesc(
            Long productId,
            LocalDateTime effectiveFrom
    );
}