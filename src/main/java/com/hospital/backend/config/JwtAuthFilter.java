package com.hospital.backend.config;

/*
 * ---------------------------------------------------------
 * IMPORTS
 * ---------------------------------------------------------
 */

/*
 * Custom service used for JWT operations like:
 * - extract username
 * - validate token
 * - generate token
 */
import com.hospital.backend.service.JwtService;

/*
 * Exception thrown when JWT token is expired.
 */
import io.jsonwebtoken.ExpiredJwtException;

/*
 * Servlet classes for handling HTTP requests/responses.
 */
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * Lombok annotation.
 *
 * Automatically creates constructor for final fields.
 */
import lombok.RequiredArgsConstructor;

/*
 * Used by Spring for null safety.
 */
import org.springframework.lang.NonNull;

/*
 * Authentication object used by Spring Security.
 */
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/*
 * Stores authentication information of current logged-in user.
 */
import org.springframework.security.core.context.SecurityContextHolder;

/*
 * Represents logged-in user details.
 */
import org.springframework.security.core.userdetails.UserDetails;

/*
 * Service used to load user from database.
 */
import org.springframework.security.core.userdetails.UserDetailsService;

/*
 * Builds request-specific authentication details.
 */
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

/*
 * Marks this class as Spring Bean.
 */
import org.springframework.stereotype.Component;

/*
 * Special Spring Security filter.
 *
 * Executes ONCE for every request.
 */
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*
 * ---------------------------------------------------------
 * CLASS: JwtAuthFilter
 * ---------------------------------------------------------
 *
 * PURPOSE:
 * This filter checks every incoming request for JWT token.
 *
 * If token is valid:
 * - user becomes authenticated
 * - request proceeds securely
 *
 * If token is invalid/expired:
 * - request is rejected
 *
 * ---------------------------------------------------------
 * VERY IMPORTANT CLASS
 * ---------------------------------------------------------
 *
 * This is one of the CORE SECURITY CLASSES
 * in JWT Authentication system.
 *
 * Without this:
 * - backend cannot identify logged-in users
 * - secured APIs cannot work
 *
 * ---------------------------------------------------------
 * FLOW
 * ---------------------------------------------------------
 *
 * Client Login
 *      |
 *      v
 * Server generates JWT token
 *      |
 *      v
 * Frontend stores token
 *      |
 *      v
 * Frontend sends token in Authorization header
 *      |
 *      v
 * JwtAuthFilter intercepts request
 *      |
 *      v
 * Validate token
 *      |
 *      +---- invalid -> reject request
 *      |
 *      +---- valid -> authenticate user
 *                      |
 *                      v
 *                  Controller executes
 *
 * ---------------------------------------------------------
 * EXAMPLE REQUEST
 * ---------------------------------------------------------
 *
 * Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 *
 * ---------------------------------------------------------
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /*
     * ---------------------------------------------------------
     * JwtService Dependency
     * ---------------------------------------------------------
     *
     * Used for:
     * - extracting username from token
     * - validating token
     * - checking expiration
     */
    private final JwtService jwtService;

    /*
     * ---------------------------------------------------------
     * UserDetailsService Dependency
     * ---------------------------------------------------------
     *
     * Loads user information from database.
     *
     * Usually connected with:
     * - UserRepository
     * - User Entity
     *
     * Example:
     * loadUserByUsername("ashish")
     */
    private final UserDetailsService userDetailsService;

    /*
     * ---------------------------------------------------------
     * doFilterInternal()
     * ---------------------------------------------------------
     *
     * Main method executed for EVERY request.
     *
     * This is the HEART of JWT authentication.
     *
     * PARAMETERS:
     *
     * request  -> incoming HTTP request
     * response -> outgoing HTTP response
     * filterChain -> next filter in Spring Security chain
     *
     * ---------------------------------------------------------
     */
    @Override
    protected void doFilterInternal(

            @NonNull HttpServletRequest request,

            @NonNull HttpServletResponse response,

            @NonNull FilterChain filterChain

    ) throws ServletException, IOException {

        /*
         * ---------------------------------------------------------
         * STEP 1: GET REQUEST PATH
         * ---------------------------------------------------------
         *
         * Example:
         *
         * /api/auth/login
         * /api/patients
         */
        String path = request.getServletPath();

        /*
         * ---------------------------------------------------------
         * STEP 2: SKIP AUTH APIs
         * ---------------------------------------------------------
         *
         * Login/Register APIs should be public.
         *
         * Because:
         * user doesn't have token before login.
         *
         * So authentication check is skipped.
         *
         * Example:
         *
         * /api/auth/login
         * /api/auth/register
         *
         * filterChain.doFilter()
         * means continue request processing.
         */
        if (path.startsWith("/api/auth")) {

            filterChain.doFilter(request, response);

            return;
        }

        /*
         * ---------------------------------------------------------
         * STEP 3: READ AUTHORIZATION HEADER
         * ---------------------------------------------------------
         *
         * Example Header:
         *
         * Authorization: Bearer eyJhbGc...
         */
        final String authHeader = request.getHeader("Authorization");

        /*
         * ---------------------------------------------------------
         * STEP 4: CHECK TOKEN EXISTS
         * ---------------------------------------------------------
         *
         * If:
         * - header missing
         * - token format invalid
         *
         * then continue without authentication.
         *
         * "Bearer " prefix is standard JWT format.
         */
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            return;
        }

        /*
         * ---------------------------------------------------------
         * STEP 5: EXTRACT JWT TOKEN
         * ---------------------------------------------------------
         *
         * Removes "Bearer "
         *
         * Example:
         *
         * Bearer abc.xyz.123
         *
         * becomes:
         *
         * abc.xyz.123
         */
        final String jwt = authHeader.substring(7);

        String username;

        try {

            /*
             * ---------------------------------------------------------
             * STEP 6: EXTRACT USERNAME FROM TOKEN
             * ---------------------------------------------------------
             *
             * JwtService reads payload from token.
             *
             * Example payload:
             *
             * {
             *   "sub": "ashish",
             *   "exp": 123456789
             * }
             *
             * username becomes:
             * "ashish"
             */
            username = jwtService.extractUsername(jwt);

        } catch (ExpiredJwtException e) {

            /*
             * ---------------------------------------------------------
             * STEP 7: HANDLE EXPIRED TOKEN
             * ---------------------------------------------------------
             *
             * If token expired:
             * return 401 Unauthorized.
             */
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            response.getWriter().write("Token expired");

            return;
        }

        /*
         * ---------------------------------------------------------
         * STEP 8: AUTHENTICATE USER
         * ---------------------------------------------------------
         *
         * Conditions:
         *
         * 1. username exists
         * 2. no authentication already present
         */
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            /*
             * ---------------------------------------------------------
             * LOAD USER FROM DATABASE
             * ---------------------------------------------------------
             *
             * Example:
             *
             * SELECT * FROM users WHERE username='ashish'
             */
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            /*
             * ---------------------------------------------------------
             * VALIDATE TOKEN
             * ---------------------------------------------------------
             *
             * Checks:
             * - token belongs to same user
             * - token not expired
             * - token signature valid
             */
            if (jwtService.isTokenValid(jwt, userDetails)) {

                /*
                 * ---------------------------------------------------------
                 * CREATE AUTHENTICATION OBJECT
                 * ---------------------------------------------------------
                 *
                 * This tells Spring Security:
                 *
                 * "User is authenticated"
                 *
                 * Contains:
                 * - user details
                 * - authorities/roles
                 */
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(

                                userDetails,

                                null,

                                userDetails.getAuthorities()

                        );

                /*
                 * ---------------------------------------------------------
                 * ATTACH REQUEST DETAILS
                 * ---------------------------------------------------------
                 *
                 * Adds extra request information:
                 * - IP address
                 * - session info
                 */
                authToken.setDetails(

                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)

                );

                /*
                 * ---------------------------------------------------------
                 * STORE AUTHENTICATION IN SECURITY CONTEXT
                 * ---------------------------------------------------------
                 *
                 * MOST IMPORTANT STEP
                 * ---------------------------------------------------------
                 *
                 * After this line:
                 *
                 * Spring Security considers user LOGGED IN.
                 *
                 * Controller can now access:
                 * - authenticated username
                 * - roles
                 * - permissions
                 */
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        /*
         * ---------------------------------------------------------
         * STEP 9: CONTINUE REQUEST FLOW
         * ---------------------------------------------------------
         *
         * Request now proceeds to:
         *
         * - next filter
         * - controller
         * - service
         * - repository
         */
        filterChain.doFilter(request, response);
    }
}