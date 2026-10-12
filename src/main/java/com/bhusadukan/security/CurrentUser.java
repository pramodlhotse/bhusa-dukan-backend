package com.bhusadukan.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public Long getUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof JwtAuthenticationToken jwtAuth)
                || !authentication.isAuthenticated()) {
            throw new IllegalStateException(
                    "An authenticated JWT user is required");
        }

        String subject = jwtAuth.getToken().getSubject();

        try {
            return Long.valueOf(subject);
        } catch (NumberFormatException exception) {
            throw new IllegalStateException(
                    "Invalid user ID in authentication token");
        }
    }
}
