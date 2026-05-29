package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.AuthRequest;
import com.hospital.backend.dto.AuthResponse;
import com.hospital.backend.dto.RegisterRequest;
import com.hospital.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Login and Register APIs. These are PUBLIC — no token required.")
public class AuthController {

	private final AuthService authService;

	/*
	 * POST /api/auth/login
	 *
	 * Public API — no JWT required.
	 * 
	 * @SecurityRequirements({}) overrides the global JWT requirement.
	 */
	@Operation(summary = "Login", description = "Authenticate with username and password. Returns a JWT token to use in all other APIs.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful — JWT token returned", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class), examples = @ExampleObject(value = """
					{
					  "success": true,
					  "message": "Login successful",
					  "data": {
					    "token": "eyJhbGciOiJIUzI1NiJ9...",
					    "username": "admin",
					    "role": "ADMIN"
					  },
					  "timestamp": "2024-01-01T10:00:00"
					}
					"""))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid username or password") })
	@SecurityRequirements // Marks this endpoint as PUBLIC in Swagger UI
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
		log.info("Login attempt: username={}", request.getUsername());
		AuthResponse response = authService.login(request);
		log.info("Login successful: username={}", request.getUsername());
		return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
	}

	/*
	 * POST /api/auth/register
	 *
	 * Public API — no JWT required.
	 */
	@Operation(summary = "Register new user", description = "Register a new system user (ADMIN, DOCTOR, RECEPTIONIST, PHARMACIST). Returns a JWT token.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered successfully"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Username or email already taken / Validation failed") })
	@SecurityRequirements // Marks this endpoint as PUBLIC in Swagger UI
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
		log.info("Register: username={} role={}", request.getUsername(), request.getRole());
		AuthResponse response = authService.register(request.getUsername(), request.getPassword(), request.getEmail(),
				request.getRole());
		log.info("Registered: username={}", request.getUsername());
		return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
	}
}