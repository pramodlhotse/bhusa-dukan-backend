package com.bhusadukan.dto;

public record CustomerLoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String name,
        String email,
        String role
) {
}
