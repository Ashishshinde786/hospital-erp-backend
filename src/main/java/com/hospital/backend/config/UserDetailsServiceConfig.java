package com.hospital.backend.config;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This class tells Spring Security:

How to load a user from database.

Very important.

Spring Security asks:

"Someone is trying to login.

How do I find this user?"

This class answers that.

---------------------------------------------------------

Without this:

AuthenticationManager

cannot find users.

Login fails.

JWT filter fails.

Security breaks.

---------------------------------------------------------
*/

import com.hospital.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

/*
@Configuration

Spring loads this class at startup.

Creates beans from methods
marked with @Bean.
*/
@Configuration

/*
 * Generates constructor:
 * 
 * UserDetailsServiceConfig( UserRepository userRepository )
 * 
 * Spring injects repository.
 */
@RequiredArgsConstructor
public class UserDetailsServiceConfig {

	/*
	 * Relationship:
	 * 
	 * this class uses repository.
	 * 
	 * Repository talks to DB.
	 */
	private final UserRepository userRepository;

	/*
	 * Creates UserDetailsService bean.
	 * 
	 * This is VERY important.
	 * 
	 * Spring Security automatically uses this bean for authentication.
	 * 
	 * It asks this bean:
	 * 
	 * "load user by username"
	 */
	@Bean
	public UserDetailsService userDetailsService() {

		/*
		 * Returning lambda implementation.
		 * 
		 * Equivalent to:
		 * 
		 * public UserDetails loadUserByUsername( String username )
		 * 
		 * because:
		 * 
		 * UserDetailsService is functional interface.
		 */

		return username ->

		/*
		 * Query database:
		 * 
		 * SELECT * FROM users WHERE username=?
		 * 
		 */
		userRepository.findByUsername(username)

				/*
				 * Optional<User>
				 * 
				 * If user not found:
				 * 
				 * throw exception.
				 */
				.orElseThrow(

						() ->

						new UsernameNotFoundException(

								"User not found: " + username

						));

		/*
		 * If found:
		 * 
		 * returns User entity.
		 * 
		 * Your User entity implements:
		 * 
		 * UserDetails
		 * 
		 * so Spring Security accepts it.
		 */
	}

}