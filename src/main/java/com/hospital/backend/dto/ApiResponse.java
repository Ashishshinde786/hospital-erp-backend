package com.hospital.backend.dto;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This class standardizes API responses.

Every controller returns responses in SAME format.

Without this:

Every API may return different JSON.

Messy.

Inconsistent.

Hard for frontend.

---------------------------------------------------------

With this:

ALL APIs return:

success

message

data

timestamp

Same structure everywhere.

=========================================================
*/

import lombok.*;

import java.time.LocalDateTime;

/*
@Data generates:

getters

setters

toString()

equals()

hashCode()
*/
@Data

/*
 * @Builder
 * 
 * Lets us build objects like:
 * 
 * ApiResponse.builder() .success(true) .message("Done") .build();
 */
@Builder

/*
 * Creates empty constructor.
 * 
 * Needed by Jackson sometimes.
 */
@NoArgsConstructor

/*
 * Creates full constructor.
 */
@AllArgsConstructor
public class ApiResponse<T> {

	/*
	 * Generic Type T
	 * 
	 * Means:
	 * 
	 * T can be:
	 * 
	 * PatientDTO
	 * 
	 * DoctorDTO
	 * 
	 * List<PatientDTO>
	 * 
	 * Long
	 * 
	 * Anything
	 */
	private T data;

	/*
	 * True or false
	 * 
	 * Did request succeed?
	 */
	private boolean success;

	/*
	 * Human-readable message.
	 * 
	 * Example:
	 * 
	 * "Patient created"
	 * 
	 * "Login successful"
	 * 
	 * "Validation failed"
	 */
	private String message;

	/*
	 * When response was generated.
	 */
	private LocalDateTime timestamp;

	/*
	 * ================================================== SUCCESS RESPONSE FACTORY
	 * METHOD ==================================================
	 * 
	 * Creates success response.
	 * 
	 * Instead of:
	 * 
	 * new ApiResponse(...)
	 * 
	 * we do:
	 * 
	 * ApiResponse.success(...)
	 * 
	 * Cleaner. ==================================================
	 */
	public static <T> ApiResponse<T> success(

			T data,

			String message

	) {

		return ApiResponse.<T>builder()

				.success(true)

				.message(message)

				.data(data)

				.timestamp(LocalDateTime.now())

				.build();
	}

	/*
	 * ================================================== ERROR RESPONSE FACTORY
	 * METHOD ==================================================
	 * 
	 * Creates error response. ==================================================
	 */
	public static <T> ApiResponse<T> error(

			String message

	) {

		return ApiResponse.<T>builder()

				.success(false)

				.message(message)

				.timestamp(LocalDateTime.now())

				.build();
	}

}