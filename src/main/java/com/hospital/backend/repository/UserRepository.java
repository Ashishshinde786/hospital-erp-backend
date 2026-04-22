package com.hospital.backend.repository;

import com.hospital.backend.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

/*
=========================================================
REPOSITORY: UserRepository

Purpose:

Database access layer for USER / SECURITY module.

Handles:

- Find users for login

- Check duplicate usernames

- Check duplicate emails

Works with:

User entity

Primary key:

Long

This repository is heavily used by:

- Spring Security

- Authentication

- JWT login flow

=========================================================
*/

@Repository

public interface UserRepository

		extends JpaRepository<User, Long> {

	/*
	 * ===================================================== Find user by username.
	 * 
	 * Used during LOGIN.
	 * 
	 * Example:
	 * 
	 * login:
	 * 
	 * username=admin
	 * 
	 * system must find user.
	 * 
	 * Returns:
	 * 
	 * Optional<User>
	 * 
	 * not User directly.
	 * 
	 * Why?
	 * 
	 * User may not exist.
	 * 
	 * Optional prevents NullPointer problems.
	 * 
	 * SQL idea:
	 * 
	 * SELECT * FROM users WHERE username=?
	 * =====================================================
	 */
	Optional<User>

			findByUsername(

					String username

	);

	/*
	 * ===================================================== Check if username
	 * already exists.
	 * 
	 * Used during REGISTER.
	 * 
	 * Example:
	 * 
	 * username:
	 * 
	 * admin
	 * 
	 * already taken?
	 * 
	 * true
	 * 
	 * reject registration.
	 * 
	 * Returns:
	 * 
	 * true/false
	 * 
	 * lightweight validation query.
	 * =====================================================
	 */
	boolean existsByUsername(

			String username

	);

	/*
	 * ===================================================== Check duplicate email.
	 * 
	 * Prevent:
	 * 
	 * two users using same email.
	 * 
	 * Used during registration.
	 * 
	 * Returns:
	 * 
	 * true/false =====================================================
	 */
	boolean existsByEmail(

			String email

	);

}