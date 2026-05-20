package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.AuthRequest;
import com.hospital.backend.dto.AuthResponse;
import com.hospital.backend.dto.RegisterRequest;
import com.hospital.backend.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt: username={}", request.getUsername());
        AuthResponse response = authService.login(request);
        log.info("Login successful: username={}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    // POST /api/auth/register
    // FIX: Changed @RequestParam → @RequestBody with a RegisterRequest DTO.
    // Sending credentials as query params is a security risk (they appear in
    // server logs and browser history). A JSON body is safer.
    @PostMapping("/register")
    // @PreAuthorize("hasRole('ADMIN')") — uncomment to restrict registration to admins
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register: username={} role={}", request.getUsername(), request.getRole());
        AuthResponse response = authService.register(
                request.getUsername(), request.getPassword(),
                request.getEmail(),    request.getRole());
        log.info("Registered: username={}", request.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
    }
}