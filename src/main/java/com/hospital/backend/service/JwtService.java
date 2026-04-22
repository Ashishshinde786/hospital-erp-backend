package com.hospital.backend.service;

import io.jsonwebtoken.Claims;

import io.jsonwebtoken.Jwts;

import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;

import java.util.Date;

import java.util.HashMap;

import java.util.Map;

import java.util.function.Function;

/*
=====================================================

JWT SERVICE

Token Engine

Handles:

1 Generate JWT

2 Sign JWT

3 Validate JWT

4 Extract username

5 Extract claims

6 Check expiration

=====================================================
*/

@Service
public class JwtService {

	/*
	 * ========================================== LOAD SECRET KEY FROM
	 * 
	 * application.properties
	 * 
	 * app.jwt.secret=... ==========================================
	 */
	@Value("${app.jwt.secret}")

	private String secretKey;

	/*
	 * Token expiration
	 * 
	 * Example:
	 * 
	 * 86400000
	 * 
	 * = 24 hours
	 */
	@Value("${app.jwt.expiration}")

	private long jwtExpiration;

	/*
	 * ========================================== GENERATE JWT TOKEN
	 * ==========================================
	 * 
	 * Called after login.
	 */
	public String generateToken(

			UserDetails userDetails

	) {

		/*
		 * Custom JWT payload data
		 * 
		 * called claims
		 */
		Map<String, Object> claims =

				new HashMap<>();

		/*
		 * Add role into token
		 * 
		 * ROLE_ADMIN
		 * 
		 * ROLE_DOCTOR
		 */
		claims.put(

				"role",

				userDetails

						.getAuthorities()

						.iterator()

						.next()

						.getAuthority());

		/*
		 * Build token.
		 */
		return buildToken(

				claims,

				userDetails,

				jwtExpiration);

	}

	/*
	 * ========================================== BUILD ACTUAL TOKEN
	 * ==========================================
	 */
	private String buildToken(

			Map<String, Object> extraClaims,

			UserDetails userDetails,

			long expiration

	) {

		return Jwts.builder()

				/*
				 * put custom claims
				 */
				.claims(extraClaims)

				/*
				 * username stored as subject
				 */
				.subject(

						userDetails.getUsername()

				)

				/*
				 * issued now
				 */
				.issuedAt(

						new Date(

								System.currentTimeMillis()

						))

				/*
				 * expires later
				 */
				.expiration(

						new Date(

								System.currentTimeMillis()

										+

										expiration

						)

				)

				/*
				 * digitally sign token
				 * 
				 * VERY IMPORTANT
				 */
				.signWith(

						getSigningKey()

				)

				/*
				 * convert into JWT string
				 */
				.compact();
	}

	/*
	 * ========================================== VALIDATE TOKEN
	 * ==========================================
	 * 
	 * Checks:
	 * 
	 * username correct?
	 * 
	 * token expired?
	 * 
	 */
	public boolean isTokenValid(

			String token,

			UserDetails userDetails

	) {

		/*
		 * extract username from token
		 */
		final String username =

				extractUsername(token);

		/*
		 * valid if:
		 * 
		 * same user
		 * 
		 * AND
		 * 
		 * not expired
		 */
		return

		username.equals(

				userDetails.getUsername()

		)

				&&

				!isTokenExpired(token);
	}

	/*
	 * ========================================== EXTRACT USERNAME
	 * 
	 * from JWT ==========================================
	 */
	public String extractUsername(

			String token

	) {

		return extractClaim(

				token,

				Claims::getSubject

		);
	}

	/*
	 * ========================================== CHECK EXPIRED?
	 * ==========================================
	 */
	private boolean isTokenExpired(

			String token

	) {

		return

		extractExpiration(token)

				.before(

						new Date()

				);
	}

	/*
	 * ========================================== GET EXPIRATION DATE
	 * ==========================================
	 */
	private Date extractExpiration(

			String token

	) {

		return extractClaim(

				token,

				Claims::getExpiration

		);
	}

	/*
	 * ========================================== GENERIC CLAIM EXTRACTION
	 * 
	 * reusable method ==========================================
	 */
	public <T> T extractClaim(

			String token,

			Function<Claims, T> claimsResolver

	) {

		return claimsResolver.apply(

				extractAllClaims(token)

		);
	}

	/*
	 * ========================================== PARSE TOKEN
	 * 
	 * VERIFY SIGNATURE
	 * 
	 * READ PAYLOAD ==========================================
	 */
	private Claims extractAllClaims(

			String token

	) {

		return Jwts.parser()

				/*
				 * verify digital signature
				 */
				.verifyWith(

						getSigningKey()

				)

				.build()

				/*
				 * parse token
				 */
				.parseSignedClaims(token)

				/*
				 * get payload claims
				 */
				.getPayload();

	}

	/*
	 * ========================================== CREATE SIGNING KEY
	 * ==========================================
	 */
	private SecretKey getSigningKey() {

		/*
		 * Convert secret string
		 * 
		 * -> bytes
		 */
		byte[] keyBytes =

				secretKey.getBytes(

						StandardCharsets.UTF_8

				);

		/*
		 * Generate cryptographic key
		 */
		return Keys.hmacShaKeyFor(

				keyBytes

		);
	}

}