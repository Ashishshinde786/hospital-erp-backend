package com.hospital.backend.entity;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

/*
=========================================================
@Entity
=========================================================

Marks this class as JPA Entity.

Means:

This class maps to database table.

Without @Entity:

Spring treats it as normal Java class.

With @Entity:

Hibernate manages it.
=========================================================
*/
@Entity

/*
 * =========================================================
 * 
 * @Table =========================================================
 * 
 * Maps class to DB table.
 * 
 * This:
 * 
 * appointments
 * 
 * becomes:
 * 
 * table name. =========================================================
 */
@Table(name = "appointments")

/*
 * Lombok: getters setters toString equals hashCode
 */
@Data

/*
 * Builder Pattern
 */
@Builder

@NoArgsConstructor

@AllArgsConstructor

public class Appointment {

	/*
	 * ========================================================= PRIMARY KEY
	 * =========================================================
	 * 
	 * Maps to:
	 * 
	 * appointments.id
	 * 
	 * AUTO_INCREMENT in MySQL.
	 * =========================================================
	 */
	@Id

	/*
	 * Database generates IDs.
	 * 
	 * 1
	 * 
	 * 2
	 * 
	 * 3
	 * 
	 * ...
	 */
	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

	/*
	 * ========================================================= RELATIONSHIP: Many
	 * Appointments -> One Patient
	 * =========================================================
	 * 
	 * One patient can have:
	 * 
	 * many appointments
	 * 
	 * But each appointment belongs to:
	 * 
	 * one patient
	 * 
	 * Relationship:
	 * 
	 * ManyToOne =========================================================
	 */
	@ManyToOne(

			/*
			 * Lazy loading:
			 * 
			 * Don't load patient immediately.
			 * 
			 * Load only when needed.
			 */
			fetch = FetchType.LAZY

	)

	/*
	 * Foreign key:
	 * 
	 * patient_id
	 * 
	 * nullable=false
	 * 
	 * means:
	 * 
	 * patient required
	 */
	@JoinColumn(

			name = "patient_id",

			nullable = false

	)

	private Patient patient;

	/*
	 * ========================================================= RELATIONSHIP: Many
	 * Appointments -> One Doctor
	 * =========================================================
	 * 
	 * One doctor:
	 * 
	 * many appointments
	 * 
	 * One appointment:
	 * 
	 * one doctor =========================================================
	 */
	@ManyToOne(

			fetch = FetchType.LAZY

	)

	@JoinColumn(

			name = "doctor_id",

			nullable = false

	)

	private Doctor doctor;

	/*
	 * ========================================================= Appointment
	 * DateTime =========================================================
	 * 
	 * Stores:
	 * 
	 * Date + Time
	 * 
	 * 2026-04-25 10:30 =========================================================
	 */
	@Column(

			nullable = false

	)

	private LocalDateTime appointmentDateTime;

	/*
	 * ========================================================= Appointment Status
	 * =========================================================
	 * 
	 * Stored as String.
	 * 
	 * NOT ordinal number. =========================================================
	 */
	@Enumerated(

	EnumType.STRING

	)

	private AppointmentStatus status;

	/*
	 * ========================================================= Reason for visit
	 * =========================================================
	 * 
	 * Examples:
	 * 
	 * Fever
	 * 
	 * Consultation
	 * 
	 * Chest pain =========================================================
	 */
	@Column(

			columnDefinition = "TEXT"

	)

	private String reason;

	/*
	 * Doctor notes.
	 * 
	 * Large text.
	 */
	@Column(

			columnDefinition = "TEXT"

	)

	private String notes;

	/*
	 * ========================================================= Audit Field
	 * =========================================================
	 * 
	 * Creation timestamp. =========================================================
	 */
	@Column(

			updatable = false

	)

	private LocalDateTime createdAt;

	/*
	 * ========================================================= Lifecycle Callback
	 * =========================================================
	 * 
	 * Automatically runs BEFORE INSERT.
	 * 
	 * Very important. =========================================================
	 */
	@PrePersist

	protected void onCreate() {

		/*
		 * Auto set created timestamp
		 */
		createdAt = LocalDateTime.now();

		/*
		 * Default status if null
		 */
		if (status == null)

			status = AppointmentStatus.SCHEDULED;
	}

	/*
	 * ========================================================= ENUM
	 * =========================================================
	 * 
	 * Allowed statuses only.
	 * =========================================================
	 */
	public enum AppointmentStatus {

		SCHEDULED,

		CONFIRMED,

		COMPLETED,

		CANCELLED,

		NO_SHOW

	}

}