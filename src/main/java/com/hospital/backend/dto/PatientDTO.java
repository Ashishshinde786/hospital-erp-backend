package com.hospital.backend.dto;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This DTO carries patient data
between:

Client (Postman / Angular)

Controller

Service

Entity

Database

---------------------------------------------------------

Used for:

Register patient

Update patient

Get patient details

Search patients

Patient master records

=========================================================
*/

import com.hospital.backend.entity.Patient.Gender;

import jakarta.validation.constraints.*;

import lombok.*;

import java.time.LocalDate;

/*
@Data

Generates:

getters

setters

toString()

equals()

hashCode()
*/
@Data

/*
 * Builder Pattern
 * 
 * PatientDTO.builder() .firstName("Ashish") .build();
 */
@Builder

/*
 * Empty constructor
 * 
 * Needed by JSON/Jackson.
 */
@NoArgsConstructor

/*
 * Full constructor
 */
@AllArgsConstructor

public class PatientDTO {

	/*
	 * ================================================== PRIMARY KEY
	 * ================================================== Database generated ID
	 * ==================================================
	 */
	private Long id;

	/*
	 * ================================================== FIRST NAME
	 * ==================================================
	 * 
	 * Required.
	 * 
	 * Cannot be:
	 * 
	 * null
	 * 
	 * empty
	 * 
	 * spaces
	 * 
	 * Examples:
	 * 
	 * Ashish
	 * 
	 * Rahul ==================================================
	 */
	@NotBlank(message = "First name is required")

	private String firstName;

	/*
	 * ================================================== LAST NAME
	 * ================================================== Required.
	 * ==================================================
	 */
	@NotBlank(message = "Last name is required")

	private String lastName;

	/*
	 * ================================================== DATE OF BIRTH
	 * ==================================================
	 * 
	 * Stores birth date.
	 * 
	 * Example:
	 * 
	 * 1997-04-20
	 * 
	 * Used for:
	 * 
	 * Age calculation
	 * 
	 * Eligibility
	 * 
	 * Medical logic ==================================================
	 */
	@NotNull(message = "Date of birth is required")

	private LocalDate dateOfBirth;

	/*
	 * ================================================== GENDER
	 * ==================================================
	 * 
	 * Enum:
	 * 
	 * MALE
	 * 
	 * FEMALE
	 * 
	 * OTHER
	 * 
	 * Controlled values only. ==================================================
	 */
	private Gender gender;

	/*
	 * ================================================== PHONE NUMBER
	 * ==================================================
	 * 
	 * Regex validation:
	 * 
	 * ^[0-9]{10}$
	 * 
	 * Meaning:
	 * 
	 * exactly 10 digits
	 * 
	 * Valid:
	 * 
	 * 9876543210
	 * 
	 * Invalid:
	 * 
	 * 12345
	 * 
	 * abc123
	 * 
	 * 987654321011 ==================================================
	 */
	@Pattern(

			regexp = "^[0-9]{10}$",

			message = "Phone must be 10 digits"

	)

	private String phone;

	/*
	 * ================================================== EMAIL
	 * ==================================================
	 * 
	 * Email format validation.
	 * 
	 * Valid:
	 * 
	 * abc@gmail.com
	 * 
	 * Invalid:
	 * 
	 * abc.com ==================================================
	 */
	@Email(message = "Invalid email")

	private String email;

	/*
	 * Patient address
	 * 
	 * Can be large text.
	 */
	private String address;

	/*
	 * ================================================== BLOOD GROUP
	 * ==================================================
	 * 
	 * Examples:
	 * 
	 * A+
	 * 
	 * B+
	 * 
	 * O-
	 * 
	 * AB+
	 * 
	 * Important medical field. ==================================================
	 */
	private String bloodGroup;

	/*
	 * ================================================== MEDICAL HISTORY
	 * ==================================================
	 * 
	 * Stores patient history:
	 * 
	 * Diabetes
	 * 
	 * BP
	 * 
	 * Allergies
	 * 
	 * Surgeries
	 * 
	 * Critical domain field. ==================================================
	 */
	private String medicalHistory;

}