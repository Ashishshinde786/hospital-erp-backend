package com.hospital.backend.config;

/*
 * Spring annotation used to mark this class as a Configuration class.
 *
 * Meaning:
 * Spring Boot will scan this class during application startup
 * and register the beans defined inside it into the Spring IOC Container.
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Classes related to CORS configuration.
 */
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/*
 * ---------------------------------------------------------
 * CLASS: CorsConfig
 * ---------------------------------------------------------
 *
 * PURPOSE:
 * This class configures CORS (Cross-Origin Resource Sharing)
 * for the backend application.
 *
 * WHY THIS IS NEEDED:
 * Your Angular frontend runs on:
 *
 *      http://localhost:4200
 *
 * while your Spring Boot backend runs on:
 *
 *      http://localhost:8080
 *
 * Browser treats these as DIFFERENT ORIGINS
 * because port numbers are different.
 *
 * Without CORS configuration:
 * Browser blocks frontend API calls for security reasons.
 *
 * Example Error:
 *
 * "Access to fetch at 'http://localhost:8080/api/...'
 * from origin 'http://localhost:4200'
 * has been blocked by CORS policy"
 *
 * This configuration allows frontend and backend
 * to communicate safely.
 *
 * ---------------------------------------------------------
 * FLOW
 * ---------------------------------------------------------
 *
 * Angular Frontend
 *        |
 *        | HTTP Request
 *        v
 * Browser checks CORS policy
 *        |
 *        v
 * Spring Boot CorsConfig validates request
 *        |
 *        v
 * If allowed -> Request reaches Controller
 * Else -> Browser blocks request
 *
 * ---------------------------------------------------------
 * PROJECT IMPORTANCE
 * ---------------------------------------------------------
 *
 * VERY IMPORTANT in Full Stack applications.
 *
 * Used in:
 * - Angular + Spring Boot
 * - React + Spring Boot
 * - Vue + Java backend
 * - Microservices
 * - Production deployments
 *
 * ---------------------------------------------------------
 */
@Configuration
public class CorsConfig {

	/*
	 * --------------------------------------------------------- METHOD:
	 * corsConfigurationSource()
	 * ---------------------------------------------------------
	 *
	 * @Bean: Registers this method return object into Spring Container.
	 *
	 * Spring Security automatically uses this bean when handling CORS requests.
	 *
	 * Return Type: CorsConfigurationSource
	 *
	 * This object contains all CORS rules.
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		/*
		 * Create CorsConfiguration object.
		 *
		 * This object stores: - allowed origins - allowed methods - allowed headers -
		 * credentials policy
		 */
		CorsConfiguration config = new CorsConfiguration();

		/*
		 * --------------------------------------------------------- ALLOWED ORIGINS
		 * ---------------------------------------------------------
		 *
		 * Only requests coming from:
		 *
		 * http://localhost:4200
		 *
		 * are allowed.
		 *
		 * This is Angular default development server port.
		 *
		 * IMPORTANT: If frontend runs on another port, update this value.
		 *
		 * Production Example:
		 *
		 * List.of("https://myhospitalapp.com")
		 *
		 * SECURITY: Never use "*" with credentials in production.
		 */
		config.setAllowedOrigins(List.of("http://localhost:4200"));

		/*
		 * --------------------------------------------------------- ALLOWED HTTP
		 * METHODS ---------------------------------------------------------
		 *
		 * These HTTP methods are permitted from frontend.
		 *
		 * GET -> Fetch data POST -> Create data PUT -> Update full object DELETE ->
		 * Delete object OPTIONS -> Preflight request by browser PATCH -> Partial update
		 *
		 * Browser sends OPTIONS request first before actual API call in many cases.
		 */
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

		/*
		 * --------------------------------------------------------- ALLOWED HEADERS
		 * ---------------------------------------------------------
		 *
		 * "*" means all headers are allowed.
		 *
		 * Common Headers:
		 *
		 * Authorization Content-Type Accept X-Requested-With
		 *
		 * Especially important for JWT authentication.
		 */
		config.setAllowedHeaders(List.of("*"));

		/*
		 * --------------------------------------------------------- ALLOW CREDENTIALS
		 * ---------------------------------------------------------
		 *
		 * true means: browser can send credentials like:
		 *
		 * - Cookies - Authorization headers - JWT tokens - Session IDs
		 *
		 * VERY IMPORTANT for secured applications.
		 *
		 * NOTE: When allowCredentials = true, allowedOrigins cannot be "*".
		 */
		config.setAllowCredentials(true);

		/*
		 * --------------------------------------------------------- URL BASED CORS
		 * CONFIGURATION SOURCE
		 * ---------------------------------------------------------
		 *
		 * Stores CORS rules mapped to URL patterns.
		 */
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		/*
		 * --------------------------------------------------------- REGISTER CORS
		 * CONFIGURATION ---------------------------------------------------------
		 *
		 * "/**" means:
		 *
		 * Apply this CORS configuration to ALL endpoints/APIs.
		 *
		 * Examples:
		 *
		 * /api/patients /api/doctors /api/billing /api/auth/login
		 *
		 * All APIs will follow same CORS rules.
		 */
		source.registerCorsConfiguration("/**", config);

		/*
		 * Return configured CORS source object.
		 */
		return source;
	}
}