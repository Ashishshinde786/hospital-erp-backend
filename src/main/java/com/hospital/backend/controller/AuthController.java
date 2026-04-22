package com.hospital.backend.controller;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This controller handles authentication APIs.

Main responsibilities:

1) Login users

2) Register users

3) Return JWT tokens

---------------------------------------------------------

This is security entry point.

Users hit this BEFORE accessing
protected APIs.

Flow:

Login

↓

Generate JWT

↓

Use JWT to call other APIs
=========================================================
*/

import com.hospital.backend.dto.ApiResponse;

import com.hospital.backend.dto.AuthRequest;

import com.hospital.backend.dto.AuthResponse;

import com.hospital.backend.entity.User;

import com.hospital.backend.service.AuthService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

/*
@RestController

Marks class as REST API controller.

Returns JSON responses.
*/
@RestController

/*
 * Base URL
 * 
 * All endpoints start with:
 * 
 * /api/auth
 */
@RequestMapping("/api/auth")

/*
 * Generates constructor:
 * 
 * AuthController( AuthService authService )
 * 
 * Spring injects AuthService.
 */
@RequiredArgsConstructor
public class AuthController {

	/*
	 * Relationship:
	 * 
	 * Controller
	 * 
	 * -> AuthService
	 * 
	 * -> UserRepository
	 * 
	 * -> JwtService
	 * 
	 * -> AuthenticationManager
	 */
	private final AuthService authService;

	/*
	 * LOGIN API
	 * 
	 * URL:
	 * 
	 * POST /api/auth/login
	 * 
	 * This authenticates user and returns JWT token.
	 */
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(
			/*
			 * Reads JSON request body.
			 * 
			 * Example:
			 * 
			 * { "username":"admin", "password":"admin123" }
			 * 
			 * converts to:
			 * 
			 * AuthRequest object
			 */
			@RequestBody
			/*
			 * validates DTO fields.
			 */
			@Valid AuthRequest request

	) {

		/*
		 * Call service.
		 * 
		 * Actual authentication happens there.
		 */
		AuthResponse response = authService.login(request);
		/*
		 * Return response:
		 * 
		 * token
		 * 
		 * username
		 * 
		 * role
		 */
		return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
	}

	/*
	 * REGISTER API
	 * 
	 * URL:
	 * 
	 * POST /api/auth/register
	 * 
	 * Creates new user.
	 */
	@PostMapping("/register")

	/*
	 * IMPORTANT:
	 * 
	 * If enabled:
	 * 
	 * only ADMIN can create users.
	 * 
	 * Enterprise systems often do this.
	 * 
	 * Uncomment in production.
	 * 
	 * @PreAuthorize("hasRole('ADMIN')")
	 */
	// @PreAuthorize("hasRole('ADMIN')")

	public ResponseEntity<ApiResponse<AuthResponse>> register(
			/*
			 * Reads from query params:
			 * 
			 * ?username=admin
			 */
			@RequestParam String username,

			@RequestParam String password,

			@RequestParam String email,

			/*
			 * Converts:
			 * 
			 * ADMIN
			 * 
			 * into enum:
			 * 
			 * User.Role.ADMIN
			 */
			@RequestParam User.Role role

	) {

		/*
		 * Call service.
		 * 
		 * service does:
		 * 
		 * check duplicates
		 * 
		 * hash password
		 * 
		 * save user
		 * 
		 * generate token
		 */
		AuthResponse response = authService.register(username, password, email, role);
		/*
		 * return success response.
		 */
		return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
	}

}