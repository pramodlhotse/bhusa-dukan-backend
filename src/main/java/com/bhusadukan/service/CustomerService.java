package com.bhusadukan.service;

import com.bhusadukan.dto.CreateCustomerRequest;
import com.bhusadukan.dto.CustomerResponse;
import com.bhusadukan.entity.User;
import com.bhusadukan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final UserRepository userRepository;

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

        if (userRepository.existsByMobile(request.mobile())) {
            throw new IllegalArgumentException(
                    "Customer already exists with mobile: " + request.mobile()
            );
        }

        if (request.email() != null
                && !request.email().isBlank()
                && userRepository.existsByEmail(request.email())) {

            throw new IllegalArgumentException(
                    "Customer already exists with email: " + request.email()
            );
        }

        User customer = User.builder()
                .name(request.name())
                .mobile(request.mobile())
                .email(request.email())
                .role(User.Role.CUSTOMER)
                .active(true)
                .build();

        User savedCustomer = userRepository.save(customer);

        return toResponse(savedCustomer);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomer(Long customerId) {

        User customer = userRepository.findById(customerId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Customer not found: " + customerId
                        )
                );

        return toResponse(customer);
    }

    private CustomerResponse toResponse(User customer) {

        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getMobile(),
                customer.getEmail(),
                customer.getRole(),
                customer.getActive()
        );
    }
}