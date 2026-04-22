package com.hospital.backend.dto;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This class represents:

Login Request DTO

Used when user sends:

username

password

to login.

---------------------------------------------------------

Client sends:

POST /api/auth/login

with JSON:

{
 "username":"admin",
 "password":"admin123"
}

Spring converts that JSON

into this object:

AuthRequest

=========================================================
*/

import lombok.Data;

/*
@Data generates:

getUsername()

setUsername()

getPassword()

setPassword()

toString()

equals()

hashCode()
*/
@Data
public class AuthRequest {

	/*
	 * ================================================== USERNAME
	 * ==================================================
	 * 
	 * User's login ID.
	 * 
	 * Example:
	 * 
	 * admin
	 * 
	 * doctor1
	 * 
	 * reception01 ==================================================
	 */
	private String username;

	/*
	 * ================================================== PASSWORD
	 * ==================================================
	 * 
	 * Raw password entered by user.
	 * 
	 * Example:
	 * 
	 * admin123
	 * 
	 * Later compared against:
	 * 
	 * Encrypted password in DB
	 * 
	 * using BCrypt.
	 * 
	 * Very important:
	 * 
	 * This is plain text ONLY during request.
	 * 
	 * It should NEVER be stored as plain text.
	 * ==================================================
	 */
	private String password;

}