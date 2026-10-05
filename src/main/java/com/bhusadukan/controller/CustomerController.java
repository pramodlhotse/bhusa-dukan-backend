package com.bhusadukan.controller;

import com.bhusadukan.dto.CreateCustomerRequest;
import com.bhusadukan.dto.CustomerResponse;
import com.bhusadukan.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(
        name = "Customers",
        description = "APIs for managing Bhusa customers"
)
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create customer",
            description = "Creates a new customer using mobile number"
    )
    public CustomerResponse createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        return customerService.createCustomer(request);
    }
    @Operation(
            summary = "Get customer",
            description = "Returns customer details by customer ID"
    )
    @GetMapping("/{id}")
    public CustomerResponse getCustomer(
            @PathVariable Long id) {

        return customerService.getCustomer(id);
    }
}