package com.hospital.backend.exception;

import com.hospital.backend.dto.ApiResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

/*
=========================================================
CLASS: GlobalExceptionHandler

Purpose:

CENTRALIZED EXCEPTION HANDLING

Handles errors for ENTIRE application.

Instead of writing:

try{
}
catch{
}

inside every controller method,

we handle errors in ONE PLACE.

Global means:

applies to all controllers.

Examples:

AuthController

PatientController

DoctorController

BillingController

AppointmentController

ALL use this.

=========================================================
*/

@RestControllerAdvice

/*
 * @RestControllerAdvice
 * 
 * Combination of:
 * 
 * @ControllerAdvice +
 * 
 * @ResponseBody
 * 
 * Means:
 * 
 * Catch exceptions globally
 * 
 * Return JSON response automatically
 */
public class GlobalExceptionHandler {

	/*
	 * ========================================================= HANDLE:
	 * 
	 * ResourceNotFoundException
	 * 
	 * Example:
	 * 
	 * patient id 99 not found
	 * 
	 * doctor id 500 not found
	 * 
	 * appointment not found
	 * 
	 * returns:
	 * 
	 * HTTP 404 =========================================================
	 */
	@ExceptionHandler(ResourceNotFoundException.class)

	public ResponseEntity<ApiResponse<Void>> handleNotFound(

			ResourceNotFoundException ex

	) {

		/*
		 * Build response:
		 * 
		 * 404 NOT FOUND
		 */
		return ResponseEntity

				.status(HttpStatus.NOT_FOUND)

				.body(

						ApiResponse.error(ex.getMessage())

				);
	}

	/*
	 * ========================================================= HANDLE VALIDATION
	 * ERRORS
	 * 
	 * Triggered by:
	 * 
	 * @Valid
	 * 
	 * Example:
	 * 
	 * POST /patients
	 * 
	 * if phone invalid
	 * 
	 * or email invalid
	 * 
	 * or required field missing
	 * 
	 * Spring throws:
	 * 
	 * MethodArgumentNotValidException
	 * 
	 * This catches it. =========================================================
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)

	public ResponseEntity<Map<String, Object>> handleValidation(

			MethodArgumentNotValidException ex

	) {

		/*
		 * Store field level errors.
		 * 
		 * Using LinkedHashMap
		 * 
		 * preserves insertion order.
		 */
		Map<String, String> fieldErrors = new LinkedHashMap<>();

		/*
		 * Get all validation errors
		 * 
		 * Loop through them.
		 */
		ex.getBindingResult()

				.getAllErrors()

				.forEach(err -> {

					/*
					 * Extract field name
					 * 
					 * Example:
					 * 
					 * phone
					 */
					String field = ((FieldError) err).getField();

					/*
					 * Extract message
					 * 
					 * Example:
					 * 
					 * Phone must be 10 digits
					 */
					fieldErrors.put(

							field,

							err.getDefaultMessage()

				);

				});

		/*
		 * Build custom response body.
		 */
		Map<String, Object> body = new LinkedHashMap<>();

		body.put("success", false);

		body.put("message", "Validation failed");

		body.put("errors", fieldErrors);

		/*
		 * Return 400 bad request
		 */
		return ResponseEntity

				.badRequest()

				.body(body);
	}

	/*
	 * ========================================================= LOGIN FAILURE
	 * 
	 * Wrong username/password
	 * 
	 * Spring Security throws:
	 * 
	 * BadCredentialsException
	 * 
	 * We catch it.
	 * 
	 * return:
	 * 
	 * 401 Unauthorized =========================================================
	 */
	@ExceptionHandler(BadCredentialsException.class)

	public ResponseEntity<ApiResponse<Void>>

			handleBadCredentials(

					BadCredentialsException ex

	) {

		return ResponseEntity

				.status(HttpStatus.UNAUTHORIZED)

				.body(

						ApiResponse.error(

								"Invalid username or password"

						)

				);
	}

	/*
	 * ========================================================= ACCESS DENIED
	 * 
	 * User logged in
	 * 
	 * BUT lacks permission.
	 * 
	 * Example:
	 * 
	 * DOCTOR tries:
	 * 
	 * /api/admin/users
	 * 
	 * blocked.
	 * 
	 * Spring throws:
	 * 
	 * AccessDeniedException
	 * 
	 * return:
	 * 
	 * 403 Forbidden =========================================================
	 */
	@ExceptionHandler(AccessDeniedException.class)

	public ResponseEntity<ApiResponse<Void>>

			handleAccessDenied(

					AccessDeniedException ex

	) {

		return ResponseEntity

				.status(HttpStatus.FORBIDDEN)

				.body(

						ApiResponse.error(

								"Access denied: insufficient permissions"

						)

				);
	}

	/*
	 * ========================================================= HANDLE BUSINESS
	 * ERRORS
	 * 
	 * catches RuntimeException
	 * 
	 * Examples:
	 * 
	 * "Username already exists"
	 * 
	 * "Insufficient stock"
	 * 
	 * "Invoice already paid"
	 * 
	 * return:
	 * 
	 * 400 =========================================================
	 */
	@ExceptionHandler(RuntimeException.class)

	public ResponseEntity<ApiResponse<Void>>

			handleRuntime(

					RuntimeException ex

	) {

		return ResponseEntity

				.status(HttpStatus.BAD_REQUEST)

				.body(

						ApiResponse.error(

								ex.getMessage()

						)

				);
	}

	/*
	 * ========================================================= LAST RESORT
	 * 
	 * catches EVERYTHING else.
	 * 
	 * Unknown exceptions.
	 * 
	 * DB crashes
	 * 
	 * NullPointerException
	 * 
	 * SQL exceptions
	 * 
	 * unexpected bugs
	 * 
	 * return:
	 * 
	 * 500 internal server error
	 * =========================================================
	 */
	@ExceptionHandler(Exception.class)

	public ResponseEntity<ApiResponse<Void>>

			handleGeneral(

					Exception ex

	) {

		return ResponseEntity

				.status(HttpStatus.INTERNAL_SERVER_ERROR)

				.body(

						ApiResponse.error(

								"Internal server error: "

										+ ex.getMessage()

						)

				);
	}

}