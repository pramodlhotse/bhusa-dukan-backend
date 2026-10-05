package com.bhusadukan.repository;

import com.bhusadukan.entity.CustomerAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerAddressRepository
        extends JpaRepository<CustomerAddress, Long> {

    List<CustomerAddress> findByUserId(Long userId);

    Optional<CustomerAddress> findByIdAndUserId(Long id, Long userId);

    Optional<CustomerAddress> findByUserIdAndDefaultAddressTrue(Long userId);
}