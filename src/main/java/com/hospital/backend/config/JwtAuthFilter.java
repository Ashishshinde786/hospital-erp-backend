package com.hospital.backend.config;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This is a JWT Authentication Filter.

Its job:

Intercept every incoming request.

Check:

Does request contain JWT token?

If yes:

Validate token.

Extract username.

Load user from database.

Authenticate user.

Put user into SecurityContext.

Then allow request to continue.

---------------------------------------------------------

Without this filter:

Token exists...

but Spring would never validate it.

User would always be anonymous.

Protected APIs would fail.

---------------------------------------------------------

Example request:

GET /api/patients

Header:

Authorization:
Bearer eyJhbGc....

This class processes that header.
=========================================================
*/

import com.hospital.backend.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
@Component

Registers this class as Spring bean.

Spring creates:

JwtAuthFilter object

Stores in IOC container.

SecurityConfig injects it:

private final JwtAuthFilter jwtAuthFilter;

Then adds it into filter chain:

.addFilterBefore(
   jwtAuthFilter,
   UsernamePasswordAuthenticationFilter.class
)

Without @Component:

filter not created.

security breaks.
*/
@Component

/*
 * @RequiredArgsConstructor
 * 
 * Lombok generates constructor:
 * 
 * public JwtAuthFilter( JwtService jwtService, UserDetailsService
 * userDetailsService )
 * 
 * Spring uses constructor injection.
 * 
 * No manual constructor writing needed.
 */
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

	/*
	 * Relationship:
	 * 
	 * JwtAuthFilter -> uses JwtService
	 * 
	 * JwtService does:
	 * 
	 * extract username
	 * 
	 * validate token
	 * 
	 * check expiration
	 */
	private final JwtService jwtService;

	/*
	 * Loads user from database.
	 * 
	 * Uses:
	 * 
	 * UserRepository
	 * 
	 * indirectly.
	 * 
	 * Flow:
	 * 
	 * filter
	 * 
	 * -> UserDetailsService
	 * 
	 * -> UserRepository
	 * 
	 * -> users table
	 */
	private final UserDetailsService userDetailsService;

	/*
	 * Main filter method.
	 * 
	 * Executes ONCE for every request.
	 * 
	 * Every request means:
	 * 
	 * GET /patients
	 * 
	 * POST /login
	 * 
	 * POST /appointments
	 * 
	 * all pass here.
	 * 
	 * Think:
	 * 
	 * this is security checkpoint.
	 */
	@Override
	protected void doFilterInternal(

			/*
			 * incoming HTTP request
			 */
			@NonNull HttpServletRequest request,

			/*
			 * outgoing response
			 */
			@NonNull HttpServletResponse response,

			/*
			 * next filter in chain
			 */
			@NonNull FilterChain filterChain

	) throws ServletException, IOException {

		/*
		 * Read Authorization header.
		 * 
		 * Example:
		 * 
		 * Authorization: Bearer eyJ....
		 * 
		 * retrieve that value.
		 */
		final String authHeader = request.getHeader("Authorization");

		/*
		 * If no Authorization header
		 * 
		 * OR
		 * 
		 * header doesn't start with:
		 * 
		 * Bearer
		 * 
		 * then skip JWT validation.
		 * 
		 * Continue request.
		 */
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {

			/*
			 * Pass request to next filter.
			 */
			filterChain.doFilter(request, response);

			return;
		}

		/*
		 * Remove:
		 * 
		 * "Bearer "
		 * 
		 * Keep only token.
		 * 
		 * Example:
		 * 
		 * "Bearer abc123"
		 * 
		 * becomes:
		 * 
		 * "abc123"
		 */
		final String jwt = authHeader.substring(7);

		/*
		 * Extract username from JWT payload.
		 * 
		 * Token contains:
		 * 
		 * subject=username
		 */
		final String username = jwtService.extractUsername(jwt);

		/*
		 * Proceed only if:
		 * 
		 * username exists
		 * 
		 * AND
		 * 
		 * user not already authenticated
		 * 
		 * Why second check?
		 * 
		 * Avoid duplicate authentication.
		 */
		if (username != null

				&&

				SecurityContextHolder.getContext().getAuthentication() == null) {

			/*
			 * Load user from DB.
			 * 
			 * SELECT * FROM users WHERE username=?
			 * 
			 * returns User entity.
			 */
			UserDetails userDetails =

					this.userDetailsService.loadUserByUsername(username);

			/*
			 * Validate JWT:
			 * 
			 * correct signature?
			 * 
			 * not expired?
			 * 
			 * belongs to same user?
			 */
			if (jwtService.isTokenValid(jwt, userDetails)) {

				/*
				 * Create authenticated user object.
				 * 
				 * Spring Security uses this to represent logged-in user.
				 */
				UsernamePasswordAuthenticationToken authToken =

						new UsernamePasswordAuthenticationToken(

								/*
								 * principal
								 * 
								 * actual user
								 */
								userDetails,

								/*
								 * credentials
								 * 
								 * null because password already validated
								 */
								null,

								/*
								 * roles
								 * 
								 * ROLE_ADMIN etc
								 */
								userDetails.getAuthorities());

				/*
				 * Add request details.
				 * 
				 * IP
				 * 
				 * session
				 * 
				 * request metadata
				 */
				authToken.setDetails(

						new WebAuthenticationDetailsSource().buildDetails(request)

				);

				/*
				 * MOST IMPORTANT LINE
				 * 
				 * Put authenticated user into SecurityContext.
				 * 
				 * This tells Spring:
				 * 
				 * USER IS LOGGED IN
				 */
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		/*
		 * Continue request.
		 * 
		 * pass control to:
		 * 
		 * next filter
		 * 
		 * then controller
		 */
		filterChain.doFilter(request, response);
	}
}