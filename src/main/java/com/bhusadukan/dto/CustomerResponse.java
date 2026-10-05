package com.bhusadukan.dto;

import com.bhusadukan.entity.User;

public record CustomerResponse(
        Long id,
        String name,
        String mobile,
        String email,
        User.Role role,
        Boolean active
) {
}