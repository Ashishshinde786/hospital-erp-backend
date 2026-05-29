package com.hospital.backend.config;

/*
 * ---------------------------------------------------------
 * IMPORTS
 * ---------------------------------------------------------
 */

/*
 * Lombok annotation.
 *
 * Automatically creates constructor for final fields.
 */
import lombok.RequiredArgsConstructor;

/*
 * Spring Configuration annotations.
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Used for specifying HTTP methods like GET, POST, OPTIONS.
 */
import org.springframework.http.HttpMethod;

/*
 * Spring Security Authentication classes.
 */
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;

/*
 * DAO-based authentication provider.
 *
 * Uses database user details for authentication.
 */
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

/*
 * Provides AuthenticationManager from Spring Security.
 */
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

/*
 * Enables method-level security annotations.
 *
 * Example:
 * @PreAuthorize("hasRole('ADMIN')")
 */
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/*
 * Main HttpSecurity configuration class.
 */
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/*
 * Enables Spring Security web security support.
 */
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/*
 * Used for disabling default configurations.
 */
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

/*
 * Defines session creation policy.
 */
import org.springframework.security.config.http.SessionCreationPolicy;

/*
 * Loads user details from database.
 */
import org.springframework.security.core.userdetails.UserDetailsService;

/*
 * Password encryption classes.
 */
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

/*
 * Main security filter chain.
 */
import org.springframework.security.web.SecurityFilterChain;

/*
 * Default Spring Security username/password filter.
 */
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * CORS configuration source.
 */
import org.springframework.web.cors.CorsConfigurationSource;

/*
 * ---------------------------------------------------------
 * CLASS: SecurityConfig
 * ---------------------------------------------------------
 *
 * PURPOSE:
 * This is the MAIN SECURITY CONFIGURATION
 * of the entire application.
 *
 * It controls:
 * - authentication
 * - authorization
 * - JWT filter
 * - password encryption
 * - public/private APIs
 * - CORS
 * - session management
 *
 * ---------------------------------------------------------
 * MOST IMPORTANT SECURITY CLASS
 * ---------------------------------------------------------
 *
 * This class defines:
 *
 * WHO can access APIs
 * HOW authentication happens
 * WHICH APIs are public/private
 * HOW JWT is validated
 *
 * ---------------------------------------------------------
 * FLOW
 * ---------------------------------------------------------
 *
 * Incoming Request
 *        |
 *        v
 * SecurityFilterChain
 *        |
 *        v
 * JwtAuthFilter
 *        |
 *        +---- valid token -> authenticated
 *        |
 *        +---- invalid token -> blocked
 *        |
 *        v
 * Controller Access
 *
 * ---------------------------------------------------------
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /*
     * ---------------------------------------------------------
     * JwtAuthFilter Dependency
     * ---------------------------------------------------------
     *
     * Custom JWT filter created by you.
     *
     * Used to:
     * - extract JWT token
     * - validate token
     * - authenticate user
     */
    private final JwtAuthFilter jwtAuthFilter;

    /*
     * ---------------------------------------------------------
     * UserDetailsService Dependency
     * ---------------------------------------------------------
     *
     * Loads user information from database.
     *
     * Usually connected to:
     * - UserRepository
     * - User Entity
     */
    private final UserDetailsService userDetailsService;

    /*
     * ---------------------------------------------------------
     * CORS Configuration Dependency
     * ---------------------------------------------------------
     *
     * Uses CorsConfig class.
     *
     * Allows frontend/backend communication.
     */
    private final CorsConfigurationSource corsConfigurationSource;

    /*
     * ---------------------------------------------------------
     * SECURITY FILTER CHAIN
     * ---------------------------------------------------------
     *
     * MOST IMPORTANT METHOD
     * ---------------------------------------------------------
     *
     * Defines complete application security rules.
     *
     * Spring Security internally creates filters from this.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        /*
         * HttpSecurity:
         * Main object used for configuring web security.
         */
        http

                /*
                 * ---------------------------------------------------------
                 * DISABLE CSRF
                 * ---------------------------------------------------------
                 *
                 * CSRF:
                 * Cross Site Request Forgery protection.
                 *
                 * Usually disabled in JWT-based REST APIs
                 * because JWT is stateless.
                 *
                 * Mostly needed in session/cookie-based authentication.
                 */
                .csrf(AbstractHttpConfigurer::disable)

                /*
                 * ---------------------------------------------------------
                 * ENABLE CORS
                 * ---------------------------------------------------------
                 *
                 * Uses CorsConfig class.
                 *
                 * Allows Angular frontend to call backend APIs.
                 */
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                /*
                 * ---------------------------------------------------------
                 * AUTHORIZATION RULES
                 * ---------------------------------------------------------
                 *
                 * Defines:
                 * - public APIs
                 * - protected APIs
                 * - request permissions
                 */
                .authorizeHttpRequests(auth -> auth

                        /*
                         * ---------------------------------------------------------
                         * ALLOW OPTIONS REQUESTS
                         * ---------------------------------------------------------
                         *
                         * Browser sends OPTIONS request
                         * before actual API call.
                         *
                         * Required for CORS preflight requests.
                         */
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        /*
                         * ---------------------------------------------------------
                         * PUBLIC APIs
                         * ---------------------------------------------------------
                         *
                         * No authentication required.
                         *
                         * Examples:
                         * - login
                         * - register
                         * - swagger/openapi docs
                         */
                        .requestMatchers(
                                "/api/auth/**",
                                "/error",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        /*
                         * ---------------------------------------------------------
                         * ALL OTHER APIs REQUIRE AUTHENTICATION
                         * ---------------------------------------------------------
                         *
                         * User must provide valid JWT token.
                         */
                        .anyRequest().authenticated()

                )

                /*
                 * ---------------------------------------------------------
                 * STATELESS SESSION MANAGEMENT
                 * ---------------------------------------------------------
                 *
                 * VERY IMPORTANT FOR JWT
                 * ---------------------------------------------------------
                 *
                 * Spring Security will NOT create sessions.
                 *
                 * Authentication happens using JWT token
                 * on EVERY request.
                 *
                 * No server-side session storage.
                 */
                .sessionManagement(sess ->
                        sess.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS))

                /*
                 * ---------------------------------------------------------
                 * AUTHENTICATION PROVIDER
                 * ---------------------------------------------------------
                 *
                 * Defines how user authentication works.
                 */
                .authenticationProvider(authenticationProvider())

                /*
                 * ---------------------------------------------------------
                 * ADD JWT FILTER
                 * ---------------------------------------------------------
                 *
                 * Inserts JwtAuthFilter BEFORE
                 * UsernamePasswordAuthenticationFilter.
                 *
                 * WHY?
                 *
                 * JWT should be validated BEFORE
                 * Spring Security checks authentication.
                 */
                .addFilterBefore(

                        jwtAuthFilter,

                        UsernamePasswordAuthenticationFilter.class);

        /*
         * Build and return SecurityFilterChain.
         */
        return http.build();
    }

    /*
     * ---------------------------------------------------------
     * AUTHENTICATION PROVIDER
     * ---------------------------------------------------------
     *
     * Handles user authentication logic.
     *
     * DaoAuthenticationProvider:
     * - loads user from DB
     * - checks password
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        /*
         * DAO = Database Access Object
         *
         * Uses UserDetailsService internally.
         */
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider();

        /*
         * Set service used for loading users.
         */
        provider.setUserDetailsService(userDetailsService);

        /*
         * Set password encoder.
         *
         * Used to compare encrypted passwords.
         */
        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /*
     * ---------------------------------------------------------
     * AUTHENTICATION MANAGER
     * ---------------------------------------------------------
     *
     * Used during LOGIN process.
     *
     * Example:
     *
     * authenticationManager.authenticate(...)
     *
     * Usually used inside AuthService.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    /*
     * ---------------------------------------------------------
     * PASSWORD ENCODER
     * ---------------------------------------------------------
     *
     * BCryptPasswordEncoder:
     * Encrypts passwords securely.
     *
     * VERY IMPORTANT:
     * Passwords should NEVER be stored as plain text.
     *
     * Example:
     *
     * plain password:
     * admin123
     *
     * encrypted:
     * $2a$10$abcxyz...
     */
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}