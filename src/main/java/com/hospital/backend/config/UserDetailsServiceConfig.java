package com.hospital.backend.config;

/*
 * ---------------------------------------------------------
 * IMPORTS
 * ---------------------------------------------------------
 */

/*
 * Repository used to fetch users from database.
 */
import com.hospital.backend.repository.UserRepository;

/*
 * Lombok annotation.
 *
 * Automatically creates constructor for final fields.
 */
import lombok.RequiredArgsConstructor;

/*
 * Spring configuration annotations.
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Spring Security interface used for loading user details.
 */
import org.springframework.security.core.userdetails.UserDetailsService;

/*
 * Exception thrown when username is not found.
 */
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/*
 * ---------------------------------------------------------
 * CLASS: UserDetailsServiceConfig
 * ---------------------------------------------------------
 *
 * PURPOSE:
 * This class provides a custom implementation
 * of Spring Security's UserDetailsService.
 *
 * ---------------------------------------------------------
 * VERY IMPORTANT CLASS
 * ---------------------------------------------------------
 *
 * Spring Security needs a way to:
 *
 * - find user by username
 * - load user from database
 * - authenticate login request
 *
 * This class provides that logic.
 *
 * ---------------------------------------------------------
 * MAIN RESPONSIBILITY
 * ---------------------------------------------------------
 *
 * During login:
 *
 * Spring Security asks:
 *
 * "Find user with this username"
 *
 * This class answers that question
 * using UserRepository.
 *
 * ---------------------------------------------------------
 * FLOW
 * ---------------------------------------------------------
 *
 * Login Request
 *       |
 *       v
 * AuthenticationManager
 *       |
 *       v
 * DaoAuthenticationProvider
 *       |
 *       v
 * UserDetailsService
 *       |
 *       v
 * UserRepository
 *       |
 *       v
 * Database
 *
 * ---------------------------------------------------------
 */
@Configuration
@RequiredArgsConstructor
public class UserDetailsServiceConfig {

    /*
     * ---------------------------------------------------------
     * UserRepository Dependency
     * ---------------------------------------------------------
     *
     * Used to fetch users from database.
     *
     * Example query:
     *
     * SELECT * FROM users
     * WHERE username = ?
     */
    private final UserRepository userRepository;

    /*
     * ---------------------------------------------------------
     * USER DETAILS SERVICE BEAN
     * ---------------------------------------------------------
     *
     * Spring Security automatically uses this bean
     * during authentication.
     *
     * UserDetailsService is a functional interface.
     *
     * Method:
     *
     * loadUserByUsername(String username)
     *
     * ---------------------------------------------------------
     * PURPOSE
     * ---------------------------------------------------------
     *
     * Find user from database using username.
     *
     * If user exists:
     * -> return UserDetails object
     *
     * If user does not exist:
     * -> throw UsernameNotFoundException
     *
     * ---------------------------------------------------------
     */
    @Bean
    public UserDetailsService userDetailsService() {

        /*
         * Lambda expression implementation.
         *
         * Equivalent to:
         *
         * public UserDetails loadUserByUsername(String username)
         */
        return username ->

                /*
                 * ---------------------------------------------------------
                 * FIND USER BY USERNAME
                 * ---------------------------------------------------------
                 *
                 * Calls repository method:
                 *
                 * findByUsername(username)
                 *
                 * Usually translated into SQL:
                 *
                 * SELECT * FROM users
                 * WHERE username = ?
                 */
                userRepository.findByUsername(username)

                        /*
                         * ---------------------------------------------------------
                         * HANDLE USER NOT FOUND
                         * ---------------------------------------------------------
                         *
                         * If user does not exist:
                         *
                         * throw exception.
                         *
                         * Spring Security catches this exception
                         * during authentication process.
                         */
                        .orElseThrow(() ->

                                new UsernameNotFoundException(

                                        "User not found: " + username
                                ));
    }
}