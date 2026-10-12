 package com.bhusadukan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerLoginRequest(

        @NotBlank(message = "Email or mobile number is required")
        @Size(max = 150, message = "Login identifier is too long")
        String identifier,

        @NotBlank(message = "Password is required")
        @Size(max = 72, message = "Password must not exceed 72 characters")
        String password

) {
}
