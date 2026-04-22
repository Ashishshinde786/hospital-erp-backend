package com.hospital.backend.exception;

/*
=========================================================
CUSTOM EXCEPTION:
ResourceNotFoundException

Purpose:

Used when requested data does NOT exist.

Examples:

Patient id 10 not found

Doctor id 55 not found

Appointment id 100 not found

Medicine not found

Invoice not found

=========================================================
*/

/*
Extends RuntimeException

Means:

This is an UNCHECKED exception.

Java does NOT force:

try/catch

or

throws

for this.

Very common in Spring Boot.
*/
public class ResourceNotFoundException extends RuntimeException {

	/*
	 * ===================================================== CONSTRUCTOR
	 * 
	 * Accepts custom error message.
	 * 
	 * Example:
	 * 
	 * throw new ResourceNotFoundException( "Patient not found with id: 5" );
	 * 
	 * "message"
	 * 
	 * goes to parent RuntimeException
	 * =====================================================
	 */
	public ResourceNotFoundException(String message) {

		/*
		 * Calls parent constructor.
		 * 
		 * Stores exception message inside RuntimeException.
		 */
		super(message);
	}

}