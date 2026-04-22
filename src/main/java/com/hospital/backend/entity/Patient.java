package com.hospital.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/*
=========================================================
ENTITY: Patient

Purpose:
Represents a patient in Hospital ERP.

This is a CORE DOMAIN ENTITY.

Everything revolves around Patient:

Patient
 -> Appointments
 -> Billing
 -> Prescriptions
 -> Medical History
 -> Lab Reports

Without Patient entity,
Hospital ERP cannot exist.

Maps to database table:

patients
=========================================================
*/

@Entity
@Table(name = "patients")

/*
 * Lombok:
 * 
 * @Data - getters - setters - toString - equals - hashCode
 * 
 * @Builder - allows object construction:
 * 
 * Patient p = Patient.builder() .firstName("Ashish") .build();
 * 
 * @NoArgsConstructor - empty constructor needed by JPA
 * 
 * @AllArgsConstructor - full constructor
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Patient {

	/*
	 * ========================================================= PRIMARY KEY
	 * 
	 * Unique identifier.
	 * 
	 * Database:
	 * 
	 * id BIGINT AUTO_INCREMENT PRIMARY KEY
	 * 
	 * Example:
	 * 
	 * Patient 1
	 * 
	 * Patient 2 =========================================================
	 */
	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

	/*
	 * ========================================================= FIRST NAME
	 * 
	 * Required field.
	 * 
	 * Validation: Cannot be null Cannot be empty Cannot be only spaces
	 * 
	 * Invalid:
	 * 
	 * ""
	 * 
	 * "    "
	 * 
	 * null =========================================================
	 */
	@NotBlank(message = "First name is required")

	@Column(nullable = false)

	private String firstName;

	/*
	 * ========================================================= LAST NAME
	 * 
	 * Required.
	 * 
	 * Example:
	 * 
	 * Shinde =========================================================
	 */
	@NotBlank(message = "Last name is required")

	@Column(nullable = false)

	private String lastName;

	/*
	 * ========================================================= DATE OF BIRTH
	 * 
	 * Critical field.
	 * 
	 * Used for:
	 * 
	 * - Age calculation
	 * 
	 * - Pediatrics logic
	 * 
	 * - Senior citizen rules
	 * 
	 * - Insurance
	 * 
	 * - Patient identification
	 * =========================================================
	 */
	@NotNull(message = "Date of birth is required")

	private LocalDate dateOfBirth;

	/*
	 * ========================================================= ENUM
	 * 
	 * Stores:
	 * 
	 * MALE FEMALE OTHER
	 * 
	 * @Enumerated(EnumType.STRING)
	 * 
	 * Stores actual text in DB:
	 * 
	 * MALE
	 * 
	 * NOT:
	 * 
	 * 0
	 * 
	 * Much safer than ordinal.
	 * =========================================================
	 */
	@Enumerated(EnumType.STRING)

	private Gender gender;

	/*
	 * ========================================================= PHONE VALIDATION
	 * 
	 * Regex:
	 * 
	 * ^[0-9]{10}$
	 * 
	 * Means:
	 * 
	 * exactly 10 digits
	 * 
	 * Valid:
	 * 
	 * 9876543210
	 * 
	 * Invalid:
	 * 
	 * 123
	 * 
	 * abc123
	 * 
	 * 123456789012 =========================================================
	 */
	@Pattern(

			regexp = "^[0-9]{10}$",

			message = "Phone must be 10 digits"

	)

	private String phone;

	/*
	 * ========================================================= EMAIL VALIDATION
	 * 
	 * Spring validates:
	 * 
	 * valid email format
	 * 
	 * Example:
	 * 
	 * patient@gmail.com =========================================================
	 */
	@Email(message = "Invalid email")

	private String email;

	/*
	 * ========================================================= ADDRESS
	 * 
	 * @Lob
	 * 
	 * Large Object
	 * 
	 * Allows long text.
	 * 
	 * Good for:
	 * 
	 * full address
	 * 
	 * building details
	 * 
	 * city/state details =========================================================
	 */
	@Lob

	private String address;

	/*
	 * Blood group
	 * 
	 * Example:
	 * 
	 * O+
	 * 
	 * AB-
	 * 
	 * Important for:
	 * 
	 * emergencies
	 */
	private String bloodGroup;

	/*
	 * ========================================================= MEDICAL HISTORY
	 * 
	 * Very large text possible.
	 * 
	 * Example:
	 * 
	 * Diabetes
	 * 
	 * Hypertension
	 * 
	 * Previous surgeries
	 * 
	 * Allergies
	 * 
	 * So:
	 * 
	 * @Lob =========================================================
	 */
	@Lob

	private String medicalHistory;

	/*
	 * ========================================================= AUDIT FIELD
	 * 
	 * Record creation time.
	 * 
	 * updatable=false
	 * 
	 * Means:
	 * 
	 * once inserted
	 * 
	 * NEVER changed =========================================================
	 */
	@Column(updatable = false)

	private LocalDateTime createdAt;

	/*
	 * ========================================================= LAST MODIFIED TIME
	 * 
	 * changes every update.
	 * =========================================================
	 */
	private LocalDateTime updatedAt;

	/*
	 * ========================================================= JPA LIFECYCLE
	 * CALLBACK
	 * 
	 * Runs automatically
	 * 
	 * BEFORE INSERT
	 * 
	 * You do NOT call this manually.
	 * 
	 * Hibernate calls it. =========================================================
	 */
	@PrePersist

	protected void onCreate() {

		/*
		 * Set creation timestamp
		 */
		createdAt = LocalDateTime.now();

		/*
		 * Set initial update timestamp
		 */
		updatedAt = LocalDateTime.now();
	}

	/*
	 * ========================================================= JPA CALLBACK
	 * 
	 * Runs BEFORE UPDATE
	 * 
	 * Every modification:
	 * 
	 * update timestamp =========================================================
	 */
	@PreUpdate

	protected void onUpdate() {

		updatedAt = LocalDateTime.now();

	}

	/*
	 * ========================================================= INNER ENUM
	 * 
	 * Defines allowed gender values.
	 * 
	 * Prevents garbage values like:
	 * 
	 * "something random" =========================================================
	 */
	public enum Gender {

		MALE,

		FEMALE,

		OTHER

	}

}