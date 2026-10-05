package com.bhusadukan.controller;

import com.bhusadukan.dto.AddressResponse;
import com.bhusadukan.dto.CreateAddressRequest;
import com.bhusadukan.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/addresses")
@RequiredArgsConstructor
@Tag(
        name = "Customer Addresses",
        description = "APIs for managing customer delivery addresses"
)
public class AddressController {

    private final AddressService addressService;

    @Operation(
            summary = "Add customer address",
            description = "Adds a new delivery address for a customer"
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AddressResponse createAddress(
            @PathVariable Long customerId,
            @Valid @RequestBody CreateAddressRequest request) {

        return addressService.createAddress(customerId, request);
    }

    @Operation(
            summary = "Get customer addresses",
            description = "Returns all delivery addresses of a customer"
    )
    @GetMapping
    public List<AddressResponse> getAddresses(
            @PathVariable Long customerId) {

        return addressService.getAddresses(customerId);
    }

    @Operation(
            summary = "Get customer address",
            description = "Returns a specific customer address"
    )
    @GetMapping("/{addressId}")
    public AddressResponse getAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId) {

        return addressService.getAddress(customerId, addressId);
    }

    @Operation(
            summary = "Update customer address",
            description = "Updates an existing customer delivery address"
    )
    @PutMapping("/{addressId}")
    public AddressResponse updateAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId,
            @Valid @RequestBody CreateAddressRequest request) {

        return addressService.updateAddress(
                customerId,
                addressId,
                request
        );
    }

    @Operation(
            summary = "Set default address",
            description = "Sets an address as the customer's default delivery address"
    )
    @PutMapping("/{addressId}/default")
    public AddressResponse setDefaultAddress(
            @PathVariable Long customerId,
            @PathVariable Long addressId) {

        return addressService.setDefaultAddress(
                customerId,
                addressId
        );
    }
}