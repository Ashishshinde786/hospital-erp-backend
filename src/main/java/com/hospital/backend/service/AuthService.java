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

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;

	// ✅ LOGIN
	public AuthResponse login(AuthRequest request) {

		// 1. Authenticate credentials
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		// 2. Fetch user
		User user = userRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		// 3. Generate JWT
		String token = jwtService.generateToken(user);

		// 4. Return response
		return AuthResponse.builder().token(token).username(user.getUsername()).role(user.getRole().name()).build();
	}

	// ✅ REGISTER
	public AuthResponse register(String username, String password, String email, User.Role role) {

		// 1. Check username
		if (userRepository.existsByUsername(username)) {
			throw new RuntimeException("Username already taken");
		}

		// 2. Check email
		if (userRepository.existsByEmail(email)) {
			throw new RuntimeException("Email already registered");
		}

		// 3. Encrypt password
		String encryptedPassword = passwordEncoder.encode(password);

		// 4. Create user
		User user = User.builder().username(username).password(encryptedPassword).email(email).role(role).build();

		// 5. Save
		userRepository.save(user);

		// 6. Generate token
		String token = jwtService.generateToken(user);

		// 7. Return response
		return AuthResponse.builder().token(token).username(user.getUsername()).role(user.getRole().name()).build();
	}
}