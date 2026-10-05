package com.bhusadukan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateAddressRequest(

        @NotBlank(message = "Address line 1 is required")
        @Size(max = 255)
        String addressLine1,

        @Size(max = 255)
        String addressLine2,

        @Size(max = 100)
        String village,

        @NotBlank(message = "City is required")
        @Size(max = 100)
        String city,

        @NotBlank(message = "District is required")
        @Size(max = 100)
        String district,

        @Size(max = 100)
        String state,

        @NotBlank(message = "Pincode is required")
        @Pattern(
                regexp = "^[1-9][0-9]{5}$",
                message = "Invalid pincode"
        )
        String pincode,

        @Size(max = 255)
        String landmark,

        Boolean defaultAddress
) {
}