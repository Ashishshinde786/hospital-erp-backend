package com.hospital.backend.dto;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This class represents:

Login Response DTO

After successful login,

server sends back:

JWT token

username

role

---------------------------------------------------------

Client sends:

username/password

Server authenticates user

Generates JWT token

Returns this object:

AuthResponse

=========================================================
*/

import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;

/*
Generates:

getters

setters

toString()

equals()

hashCode()
*/
@Data

/*
 * Builder pattern.
 */
@Builder

/*
 * Empty constructor.
 */
@NoArgsConstructor

/*
 * Full constructor.
 */
@AllArgsConstructor
public class AuthResponse {

	/*
	 * ================================================== JWT TOKEN
	 * ==================================================
	 * 
	 * Generated after login.
	 * 
	 * Used for authentication in future requests.
	 * 
	 * Example:
	 * 
	 * Bearer eyJhbGc...
	 * 
	 * Client stores this token.
	 * 
	 * Then sends it in:
	 * 
	 * Authorization header. ==================================================
	 */
	private String token;

	/*
	 * Logged in username.
	 * 
	 * Example:
	 * 
	 * admin
	 * 
	 * doctor1
	 */
	private String username;

	/*
	 * User role.
	 * 
	 * Example:
	 * 
	 * ADMIN
	 * 
	 * DOCTOR
	 * 
	 * RECEPTIONIST
	 * 
	 * Used for authorization.
	 */
	private String role;

}