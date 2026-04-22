package com.hospital.backend.entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.*;

import lombok.*;

/*
=========================================================
@Entity
=========================================================

This class maps to database table.

Doctor Java object

↔

doctors table
=========================================================
*/
@Entity

/*
 * Maps entity to table:
 * 
 * doctors
 */
@Table(name = "doctors")

@Data

@Builder

@NoArgsConstructor

@AllArgsConstructor

public class Doctor {

	/*
	 * ========================================================= PRIMARY KEY
	 * =========================================================
	 */
	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

	/*
	 * ========================================================= Doctor first name
	 * =========================================================
	 */
	@NotBlank(message = "First name is required")

	private String firstName;

	/*
	 * Doctor last name
	 */
	@NotBlank(message = "Last name is required")

	private String lastName;

	/*
	 * ========================================================= Medical
	 * specialization
	 * 
	 * Examples:
	 * 
	 * Cardiology
	 * 
	 * Neurology
	 * 
	 * Orthopedic =========================================================
	 */
	@NotBlank(message = "Specialization is required")

	private String specialization;

	/*
	 * ========================================================= Medical License
	 * Number
	 * 
	 * Unique.
	 * 
	 * No two doctors can have same license.
	 * =========================================================
	 */
	@NotBlank(message = "License number is required")

	@Column(

			unique = true

	)

	private String licenseNumber;

	/*
	 * Doctor email
	 */
	@Email(message = "Invalid email")

	private String email;

	/*
	 * Contact number
	 */
	private String phone;

	/*
	 * Qualification
	 * 
	 * MBBS
	 * 
	 * MD
	 * 
	 * MS
	 */
	private String qualification;

	/*
	 * ========================================================= Doctor Profile/Bio
	 * 
	 * Large text field. =========================================================
	 */
	@Column(columnDefinition = "TEXT")

	private String bio;

	/*
	 * ========================================================= Doctor availability
	 * 
	 * Default:
	 * 
	 * true
	 * 
	 * Available for appointments.
	 * =========================================================
	 */
	@Builder.Default

	private boolean available = true;

	/*
	 * ========================================================= RELATIONSHIP
	 * 
	 * Many doctors
	 * 
	 * can be linked
	 * 
	 * to one User? =========================================================
	 * 
	 * Actually in real systems
	 * 
	 * usually OneToOne.
	 * 
	 * But your current model:
	 * 
	 * ManyToOne. =========================================================
	 */
	@ManyToOne(

			fetch = FetchType.LAZY

	)

	/*
	 * Foreign key:
	 * 
	 * user_id
	 */
	@JoinColumn(name = "user_id")

	private User user;

}