
package com.bhusadukan.dto;

public record CustomerRegisterResponse(
        Long id,
        String name,
        String mobile,
        String email,
        String role,
        Boolean active
) {
}
