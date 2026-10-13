package com.bhusadukan.controller;

import com.bhusadukan.dto.*;
import com.bhusadukan.service.CustomerAuthService;
import com.bhusadukan.service.CustomerLoginService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class CustomerAuthController {

    private final CustomerAuthService customerAuthService;
    private final CustomerLoginService customerLoginService;

    public CustomerAuthController(
            CustomerAuthService customerAuthService,
            CustomerLoginService customerLoginService) {
        this.customerAuthService = customerAuthService;
        this.customerLoginService = customerLoginService;
    }

    @PostMapping("/register")
    public ResponseEntity<CustomerRegisterResponse> register(
            @Valid @RequestBody CustomerRegisterRequest request) {

        CustomerRegisterResponse response =
                customerAuthService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerLoginResponse> login(
            @Valid @RequestBody CustomerLoginRequest request) {

        CustomerLoginResponse response =
                customerLoginService.login(request);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CustomerRegisterResponse> createUser(
            @Valid @RequestBody AdminCreateUserRequest request) {

        CustomerRegisterResponse response =
                customerAuthService.createUser(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

}