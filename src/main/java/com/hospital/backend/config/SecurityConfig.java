package com.hospital.backend.config;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This is the central Spring Security configuration.

This class tells Spring:

Which URLs are protected

Which roles can access them

How login authentication works

How passwords are checked

Where JWT filter runs

Whether sessions are used

Whether CORS is enabled

Whether CSRF is enabled

---------------------------------------------------------

This is SECURITY RULE BOOK.

Without this:

No authentication

No authorization

No JWT security

No role checks

No access control

---------------------------------------------------------
*/

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.AuthenticationProvider;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
@Configuration

Spring configuration class.

Spring loads this at startup.

Registers beans inside it.
*/
@Configuration

/*
 * Enables Spring Security.
 * 
 * Without this:
 * 
 * security disabled.
 */
@EnableWebSecurity

/*
 * Allows method-level security.
 * 
 * Enables:
 * 
 * @PreAuthorize
 * 
 * Example:
 * 
 * @PreAuthorize("hasRole('ADMIN')")
 */
@EnableMethodSecurity

/*
 * Generates constructor for final fields.
 * 
 * Injects dependencies automatically.
 */
@RequiredArgsConstructor
public class SecurityConfig {

	/*
	 * Your custom JWT filter.
	 * 
	 * This validates tokens.
	 */
	private final JwtAuthFilter jwtAuthFilter;

	/*
	 * CORS configuration.
	 * 
	 * Uses CorsConfig class.
	 */
	private final CorsConfig corsConfig;

	/*
	 * Loads users from database.
	 * 
	 * Uses:
	 * 
	 * UserDetailsServiceConfig
	 * 
	 * -> UserRepository
	 * 
	 * -> users table
	 */
	private final UserDetailsService userDetailsService;

	/*
	 * MAIN SECURITY RULES LIVE HERE.
	 * 
	 * This builds security filter chain.
	 * 
	 * Think:
	 * 
	 * Build security pipeline.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http

				/*
				 * Disable CSRF.
				 * 
				 * CSRF mostly needed for session/cookie based apps.
				 * 
				 * Not needed for JWT APIs.
				 * 
				 * Very common for REST.
				 */
				.csrf(AbstractHttpConfigurer::disable)

				/*
				 * Enable CORS.
				 * 
				 * Uses CorsConfig class.
				 */
				.cors(cors -> cors.configurationSource(corsConfig.corsConfigurationSource()))

				/*
				 * URL authorization rules.
				 */
				.authorizeHttpRequests(auth -> auth

						/*
						 * Public endpoints.
						 * 
						 * Anyone can access.
						 * 
						 * Needed for login.
						 */
						.requestMatchers("/api/auth/**").permitAll()

						/*
						 * Only ADMIN
						 */
						.requestMatchers("/api/admin/**").hasRole("ADMIN")

						/*
						 * Multiple roles allowed.
						 */
						.requestMatchers("/api/doctors/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")

						.requestMatchers("/api/patients/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")

						.requestMatchers("/api/appointments/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")

						.requestMatchers("/api/pharmacy/**").hasAnyRole("ADMIN", "PHARMACIST")

						.requestMatchers("/api/billing/**").hasAnyRole("ADMIN", "RECEPTIONIST")

						/*
						 * Anything not matched above:
						 * 
						 * must be authenticated.
						 */
						.anyRequest().authenticated())

				/*
				 * IMPORTANT
				 * 
				 * Stateless sessions.
				 * 
				 * No HTTP session.
				 * 
				 * No JSESSIONID.
				 * 
				 * Every request carries JWT.
				 * 
				 * Perfect for REST APIs.
				 */
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				/*
				 * How username/password authentication works.
				 */
				.authenticationProvider(authenticationProvider())

				/*
				 * Add JWT filter BEFORE
				 * 
				 * Spring login filter.
				 * 
				 * Very important.
				 * 
				 * JWT checked first.
				 */
				.addFilterBefore(

						jwtAuthFilter,

						UsernamePasswordAuthenticationFilter.class

				);

		/*
		 * Build final security chain.
		 */
		return http.build();
	}

	/*
	 * HOW LOGIN AUTHENTICATION WORKS
	 * 
	 * This handles:
	 * 
	 * username lookup
	 * 
	 * password verification
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {

		/*
		 * Default Spring provider for database authentication.
		 */
		DaoAuthenticationProvider provider =

				new DaoAuthenticationProvider();

		/*
		 * How to load users.
		 */
		provider.setUserDetailsService(userDetailsService);

		/*
		 * How passwords are checked.
		 * 
		 * compares:
		 * 
		 * raw password
		 * 
		 * vs
		 * 
		 * hashed password in DB
		 */
		provider.setPasswordEncoder(passwordEncoder());

		return provider;
	}

	/*
	 * AuthenticationManager
	 * 
	 * Used during login.
	 * 
	 * AuthService uses this:
	 * 
	 * authenticationManager.authenticate()
	 */
	@Bean
	public AuthenticationManager authenticationManager(

			AuthenticationConfiguration config

	) throws Exception {

		return config.getAuthenticationManager();
	}

	/*
	 * Password encoder bean.
	 * 
	 * Uses BCrypt.
	 * 
	 * VERY IMPORTANT.
	 * 
	 * Never store raw passwords.
	 * 
	 * Store:
	 * 
	 * $2a$10....
	 * 
	 * hashed values.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

}