package com.hospital.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/*
=========================================================
ENTITY: User

Purpose:

Represents LOGIN USERS of system.

This is NOT patient.

This is SYSTEM USER.

Examples:

Admin login

Doctor login

Receptionist login

Pharmacist login

This is used by:

- Spring Security

- Authentication

- Authorization

- JWT

- Role Based Access Control (RBAC)

Database table:

users
=========================================================
*/

@Entity

@Table(name = "users")

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

/*
 * IMPORTANT:
 * 
 * implements UserDetails
 * 
 * This is VERY important.
 * 
 * It makes this class compatible with Spring Security.
 * 
 * Spring Security understands:
 * 
 * - username
 * 
 * - password
 * 
 * - roles
 * 
 * - account status
 * 
 * because this class implements UserDetails.
 */
public class User implements UserDetails {

	/*
	 * ===================================================== PRIMARY KEY
	 * 
	 * Unique user id
	 * 
	 * Example:
	 * 
	 * 1
	 * 
	 * 2
	 * 
	 * 3 =====================================================
	 */
	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

	/*
	 * ===================================================== LOGIN USERNAME
	 * 
	 * Used during login:
	 * 
	 * admin
	 * 
	 * doctor1
	 * 
	 * pharmacist1
	 * 
	 * unique=true
	 * 
	 * means:
	 * 
	 * cannot have duplicates
	 * 
	 * BAD:
	 * 
	 * admin admin
	 * 
	 * not allowed =====================================================
	 */
	@Column(

			unique = true,

			nullable = false

	)

	private String username;

	/*
	 * ===================================================== PASSWORD
	 * 
	 * STORED HASHED.
	 * 
	 * NEVER plain text.
	 * 
	 * Stored like:
	 * 
	 * $2a$10$.....
	 * 
	 * BCrypt encoded.
	 * 
	 * During login:
	 * 
	 * entered password
	 * 
	 * vs
	 * 
	 * encoded password
	 * 
	 * are compared. =====================================================
	 */
	@Column(nullable = false)

	private String password;

	/*
	 * ===================================================== EMAIL
	 * 
	 * Required.
	 * 
	 * Used for:
	 * 
	 * user identification
	 * 
	 * password reset
	 * 
	 * notifications =====================================================
	 */
	@Column(nullable = false)

	private String email;

	/*
	 * ===================================================== USER ROLE
	 * 
	 * Defines authorization.
	 * 
	 * What can user access?
	 * 
	 * ADMIN
	 * 
	 * DOCTOR
	 * 
	 * RECEPTIONIST
	 * 
	 * PHARMACIST
	 * 
	 * Stored as String:
	 * 
	 * ADMIN
	 * 
	 * not 0 =====================================================
	 */
	@Enumerated(EnumType.STRING)

	private Role role;

	/*
	 * ===================================================== ENUM
	 * 
	 * Fixed allowed roles.
	 * 
	 * Prevents random role values.
	 * 
	 * Example:
	 * 
	 * SUPERMAN
	 * 
	 * invalid.
	 * 
	 * ADMIN
	 * 
	 * valid. =====================================================
	 */
	public enum Role {

		ADMIN,

		DOCTOR,

		RECEPTIONIST,

		PHARMACIST

	}

	/*
	 * ===================================================== SPRING SECURITY METHOD
	 * 
	 * CRITICAL METHOD
	 * 
	 * Converts our role:
	 * 
	 * ADMIN
	 * 
	 * into
	 * 
	 * ROLE_ADMIN
	 * 
	 * because Spring expects:
	 * 
	 * ROLE_ prefix
	 * 
	 * SecurityConfig:
	 * 
	 * hasRole("ADMIN")
	 * 
	 * internally checks:
	 * 
	 * ROLE_ADMIN
	 * 
	 * THIS METHOD PROVIDES THAT.
	 * =====================================================
	 */
	@Override

	public Collection<? extends GrantedAuthority> getAuthorities() {

		/*
		 * Create list of roles/authorities
		 * 
		 * Example output:
		 * 
		 * ROLE_ADMIN
		 * 
		 * ROLE_DOCTOR
		 */
		return List.of(

				new SimpleGrantedAuthority(

						"ROLE_" + role.name()

				));
	}

	/*
	 * ===================================================== ACCOUNT EXPIRY CHECK
	 * 
	 * true means:
	 * 
	 * account active.
	 * 
	 * false means:
	 * 
	 * expired account.
	 * 
	 * login blocked.
	 * 
	 * Could be useful later:
	 * 
	 * disable inactive employees.
	 * =====================================================
	 */
	@Override

	public boolean isAccountNonExpired() {

		return true;

	}

	/*
	 * ===================================================== ACCOUNT LOCK CHECK
	 * 
	 * If false:
	 * 
	 * user locked.
	 * 
	 * login denied.
	 * 
	 * Useful for:
	 * 
	 * too many failed attempts
	 * 
	 * fraud detection =====================================================
	 */
	@Override

	public boolean isAccountNonLocked() {

		return true;

	}

	/*
	 * ===================================================== PASSWORD EXPIRY CHECK
	 * 
	 * If false:
	 * 
	 * password expired.
	 * 
	 * user forced to reset password.
	 * =====================================================
	 */
	@Override

	public boolean isCredentialsNonExpired() {

		return true;

	}

	/*
	 * ===================================================== ACCOUNT ENABLED CHECK
	 * 
	 * false means:
	 * 
	 * disabled user
	 * 
	 * fired employee
	 * 
	 * suspended account
	 * 
	 * login denied. =====================================================
	 */
	@Override

	public boolean isEnabled() {

		return true;

	}

}