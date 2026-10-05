package com.bhusadukan.dto;

public record AddressResponse(
        Long id,
        Long customerId,
        String addressLine1,
        String addressLine2,
        String village,
        String city,
        String district,
        String state,
        String pincode,
        String landmark,
        Boolean defaultAddress
) {
}