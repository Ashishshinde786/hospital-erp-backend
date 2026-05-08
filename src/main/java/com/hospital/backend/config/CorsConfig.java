package com.hospital.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration config = new CorsConfiguration();

		/*
		 * Angular frontend URL
		 */
		config.setAllowedOrigins(

				List.of("http://localhost:4200"));

		/*
		 * Allowed HTTP methods
		 */
		config.setAllowedMethods(

				List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

		/*
		 * Allowed headers
		 */
		config.setAllowedHeaders(

				List.of("*"));

		/*
		 * Allow credentials
		 */
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration(

				"/**",

				config);

		return source;
	}
}