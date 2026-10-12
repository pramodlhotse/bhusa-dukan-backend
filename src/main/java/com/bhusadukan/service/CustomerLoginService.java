package com.bhusadukan.service;

import com.bhusadukan.dto.CustomerLoginRequest;
import com.bhusadukan.dto.CustomerLoginResponse;
import com.bhusadukan.entity.User;
import com.bhusadukan.repository.UserRepository;
import com.bhusadukan.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class CustomerLoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public CustomerLoginService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public CustomerLoginResponse login(CustomerLoginRequest request) {
        String identifier = request.identifier().trim();

        User user;
        if (identifier.contains("@")) {
            user = userRepository.findByEmail(
                            identifier.toLowerCase(Locale.ROOT))
                    .orElse(null);
        } else {
            user = userRepository.findByMobile(identifier)
                    .orElse(null);
        }

        if (user == null
                || !Boolean.TRUE.equals(user.getActive())
                || user.getPasswordHash() == null
                || !passwordEncoder.matches(
                request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException(
                    "Invalid email/mobile number or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return new CustomerLoginResponse(
                token,
                "Bearer",
                3600L,
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
