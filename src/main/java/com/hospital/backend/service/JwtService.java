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

@Service
public class JwtService {

	// Secret key from application.properties
	@Value("${app.jwt.secret}")
	private String secretKey;

	// Token expiration time
	@Value("${app.jwt.expiration}")
	private long jwtExpiration;

	// ✅ Generate JWT token
	public String generateToken(UserDetails userDetails) {

		Map<String, Object> claims = new HashMap<>();

		// Add role to token
		claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());

		return buildToken(claims, userDetails, jwtExpiration);
	}

	// ✅ Build token
	private String buildToken(Map<String, Object> claims, UserDetails userDetails, long expiration) {

		return Jwts.builder().claims(claims).subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration)).signWith(getSigningKey()).compact();
	}

	// ✅ Validate token
	public boolean isTokenValid(String token, UserDetails userDetails) {

		final String username = extractUsername(token);

		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	// ✅ Extract username
	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// ✅ Check token expired
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	// ✅ Get expiration date
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	// ✅ Extract any claim
	public <T> T extractClaim(String token, Function<Claims, T> resolver) {
		return resolver.apply(extractAllClaims(token));
	}

	// ✅ Parse & validate token
	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
	}

	// ✅ Generate signing key
	private SecretKey getSigningKey() {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}