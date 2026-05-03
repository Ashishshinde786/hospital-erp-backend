package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.AuthRequest;
import com.hospital.backend.dto.AuthResponse;
import com.hospital.backend.entity.User;
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

	// LOGIN
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {

		log.info("API CALL: Login attempt for username={}", request.getUsername());

		AuthResponse response = authService.login(request);

		log.info("Login successful for username={}", request.getUsername());

		return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
	}

	// REGISTER
	@PostMapping("/register")
	// @PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestParam String username,
			@RequestParam String password, @RequestParam String email, @RequestParam User.Role role) {

		log.info("API CALL: Register user username={} role={}", username, role);

		AuthResponse response = authService.register(username, password, email, role);

		log.info("User registered successfully username={}", username);

		return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
	}
}