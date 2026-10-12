package com.bhusadukan.service;

import com.bhusadukan.dto.CustomerRegisterRequest;
import com.bhusadukan.dto.CustomerRegisterResponse;
import com.bhusadukan.dto.AdminCreateUserRequest;
import com.bhusadukan.entity.User;
import com.bhusadukan.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerAuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomerRegisterResponse register(
            CustomerRegisterRequest request) {

        String mobile = request.mobile().trim();
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByMobile(mobile)) {
            throw new IllegalArgumentException(
                    "Mobile number is already registered");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        User user = User.builder()
                .name(request.name().trim())
                .mobile(mobile)
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(User.Role.CUSTOMER)
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return new CustomerRegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getMobile(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getActive()
        );
    }

    @Transactional
    public CustomerRegisterResponse createUser(
            AdminCreateUserRequest request) {

        String mobile = request.mobile().trim();
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByMobile(mobile)) {
            throw new IllegalArgumentException(
                    "Mobile number is already registered");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        User user = User.builder()
                .name(request.name().trim())
                .mobile(mobile)
                .email(email)
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(request.role())
                .active(true)
                .build();

        User savedUser = userRepository.save(user);

        return new CustomerRegisterResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getMobile(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getActive()
        );
    }

}
