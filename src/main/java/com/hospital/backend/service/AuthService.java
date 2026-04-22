package com.hospital.backend.service;

import com.hospital.backend.dto.AuthRequest;
import com.hospital.backend.dto.AuthResponse;

import com.hospital.backend.entity.User;

import com.hospital.backend.exception.ResourceNotFoundException;

import com.hospital.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

/*
========================================================

AUTH SERVICE

Business Logic Layer for Authentication.

Handles:

1 Login

2 Register user

3 Validate credentials

4 Encrypt passwords

5 Generate JWT token

6 Return authentication response

========================================================
*/

@Service

/*
 * Marks as Spring managed service bean.
 * 
 * Can inject into controller.
 */
@RequiredArgsConstructor

/*
 * Lombok creates constructor automatically for final dependencies.
 */
public class AuthService {

	/*
	 * ==================================================== DEPENDENCIES
	 * ====================================================
	 */

	/*
	 * Used for:
	 * 
	 * - Find user
	 * 
	 * - Check username exists
	 * 
	 * - Save user
	 */
	private final UserRepository userRepository;

	/*
	 * Used for hashing passwords.
	 * 
	 * Plain text:
	 * 
	 * admin123
	 * 
	 * Stored as:
	 * 
	 * $2a$10$Jkdhfjsk...
	 */
	private final PasswordEncoder passwordEncoder;

	/*
	 * Used to generate JWT token.
	 * 
	 * login successful -> create token
	 */
	private final JwtService jwtService;

	/*
	 * Spring Security engine.
	 * 
	 * Used to verify:
	 * 
	 * username correct?
	 * 
	 * password correct?
	 */
	private final AuthenticationManager authenticationManager;

	/*
	 * ==================================================== LOGIN
	 * ====================================================
	 * 
	 * Steps:
	 * 
	 * 1 Authenticate credentials
	 * 
	 * 2 Load user
	 * 
	 * 3 Generate JWT
	 * 
	 * 4 Return response
	 */
	public AuthResponse login(AuthRequest request) {

		/*
		 * STEP 1
		 * 
		 * Authenticate user.
		 * 
		 * Spring internally checks:
		 * 
		 * username exists?
		 * 
		 * password matches hashed password?
		 */
		authenticationManager.authenticate(

				new UsernamePasswordAuthenticationToken(

						request.getUsername(),

						request.getPassword()

				));

		/*
		 * If wrong password:
		 * 
		 * BadCredentialsException thrown.
		 * 
		 * stops here.
		 */

		/*
		 * STEP 2
		 * 
		 * Load user from database
		 */
		User user =

				userRepository

						.findByUsername(request.getUsername())

						.orElseThrow(

								() -> new ResourceNotFoundException(

										"User not found"

								)

						);

		/*
		 * STEP 3
		 * 
		 * Generate JWT token
		 * 
		 * Example:
		 * 
		 * eyJhbGc...
		 */
		String token =

				jwtService.generateToken(user);

		/*
		 * STEP 4
		 * 
		 * Return response to client.
		 */
		return AuthResponse.builder()

				.token(token)

				.username(user.getUsername())

				.role(user.getRole().name())

				.build();
	}

	/*
	 * ==================================================== REGISTER USER
	 * ====================================================
	 * 
	 * Steps
	 * 
	 * 1 Check duplicate username
	 * 
	 * 2 Check duplicate email
	 * 
	 * 3 Encrypt password
	 * 
	 * 4 Save user
	 * 
	 * 5 Generate JWT
	 * 
	 * 6 Return response ====================================================
	 */
	public AuthResponse register(

			String username,

			String password,

			String email,

			User.Role role

	) {

		/*
		 * STEP 1
		 * 
		 * Prevent duplicate usernames
		 */
		if (

		userRepository.existsByUsername(username)

		) {

			throw new RuntimeException(

					"Username already taken"

			);
		}

		/*
		 * STEP 2
		 * 
		 * Prevent duplicate emails
		 */
		if (

		userRepository.existsByEmail(email)

		) {

			throw new RuntimeException(

					"Email already registered"

			);
		}

		/*
		 * STEP 3
		 * 
		 * Encrypt password
		 * 
		 * NEVER store plain passwords.
		 */
		String encryptedPassword =

				passwordEncoder.encode(password);

		/*
		 * STEP 4
		 * 
		 * Create user entity
		 */
		User user =

				User.builder()

						.username(username)

						.password(encryptedPassword)

						.email(email)

						.role(role)

						.build();

		/*
		 * STEP 5
		 * 
		 * Save to database
		 */
		userRepository.save(user);

		/*
		 * STEP 6
		 * 
		 * Auto-login after register
		 * 
		 * Generate token immediately.
		 */
		String token =

				jwtService.generateToken(user);

		/*
		 * STEP 7
		 * 
		 * Return response.
		 */
		return AuthResponse.builder()

				.token(token)

				.username(user.getUsername())

				.role(user.getRole().name())

				.build();

	}

}