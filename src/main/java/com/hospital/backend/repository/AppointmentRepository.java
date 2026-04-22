package com.hospital.backend.repository;

import com.hospital.backend.entity.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/*
=========================================================
REPOSITORY: AppointmentRepository

Purpose:

DATA ACCESS LAYER

This talks to database.

Repository is responsible for:

- Fetch data

- Save data

- Delete data

- Search data

This is NOT business logic.

This is DATABASE logic.

Works on:

Appointment entity

Primary key:

Long

That is why:

JpaRepository<Appointment, Long>

=========================================================
*/

@Repository

/*
 * @Repository
 * 
 * Marks this as Spring bean.
 * 
 * Spring creates object automatically.
 * 
 * Can inject into service:
 * 
 * private final AppointmentRepository repository;
 */
public interface AppointmentRepository

		/*
		 * Extends JpaRepository
		 * 
		 * This gives built-in methods automatically:
		 * 
		 * save()
		 * 
		 * findById()
		 * 
		 * findAll()
		 * 
		 * delete()
		 * 
		 * count()
		 * 
		 * without writing SQL.
		 */
		extends JpaRepository<Appointment, Long> {

	/*
	 * ===================================================== DERIVED QUERY METHOD
	 * 
	 * Spring reads method name:
	 * 
	 * findByPatientId
	 * 
	 * and auto creates query.
	 * 
	 * It understands:
	 * 
	 * Appointment
	 * 
	 * -> patient
	 * 
	 * -> id
	 * 
	 * equivalent SQL:
	 * 
	 * SELECT * FROM appointments WHERE patient_id = ?
	 * 
	 * =====================================================
	 */
	List<Appointment>

			findByPatientId(Long patientId);

	/*
	 * ===================================================== Find appointments by
	 * doctor.
	 * 
	 * SQL idea:
	 * 
	 * SELECT * FROM appointments WHERE doctor_id = ?
	 * 
	 * Derived automatically.
	 * 
	 * No query written manually.
	 * =====================================================
	 */
	List<Appointment>

			findByDoctorId(Long doctorId);

	/*
	 * ===================================================== CUSTOM JPQL QUERY
	 * 
	 * Why custom?
	 * 
	 * Method name too complex.
	 * 
	 * easier with @Query
	 * 
	 * JPQL:
	 * 
	 * works on ENTITY objects
	 * 
	 * NOT table names.
	 * 
	 * Appointment a
	 * 
	 * means:
	 * 
	 * entity object
	 * 
	 * NOT SQL table directly.
	 * 
	 * BETWEEN:
	 * 
	 * filters appointments within date range.
	 * 
	 * ORDER BY:
	 * 
	 * earliest first. =====================================================
	 */
	@Query(

	"SELECT a FROM Appointment a " +

			"WHERE a.appointmentDateTime " + "BETWEEN :start AND :end " +

			"ORDER BY a.appointmentDateTime ASC"

	)

	List<Appointment>

			findByDateRange(

					LocalDateTime start,

					LocalDateTime end

	);

	/*
	 * ===================================================== More advanced query.
	 * 
	 * Find appointments:
	 * 
	 * for ONE doctor
	 * 
	 * within date range.
	 * 
	 * Used for:
	 * 
	 * Doctor schedule
	 * 
	 * Conflict checking
	 * 
	 * Availability check
	 * 
	 * Calendar view
	 * 
	 * Example:
	 * 
	 * Doctor 5
	 * 
	 * appointments between
	 * 
	 * Jan1-Jan31 =====================================================
	 */
	@Query(

	"SELECT a FROM Appointment a " +

			"WHERE a.doctor.id = :doctorId " +

			"AND a.appointmentDateTime " + "BETWEEN :start AND :end " +

			"ORDER BY a.appointmentDateTime ASC"

	)

	List<Appointment>

			findByDoctorAndDateRange(

					Long doctorId,

					LocalDateTime start,

					LocalDateTime end

	);

	/*
	 * ===================================================== COUNT QUERY
	 * 
	 * Returns NUMBER
	 * 
	 * not records.
	 * 
	 * Used for:
	 * 
	 * dashboard
	 * 
	 * daily appointment count
	 * 
	 * reporting
	 * 
	 * analytics
	 * 
	 * Example:
	 * 
	 * How many appointments today?
	 * =====================================================
	 */
	@Query(

	"SELECT COUNT(a) FROM Appointment a " +

			"WHERE a.appointmentDateTime " +

			"BETWEEN :start AND :end"

	)

	long countByDateRange(

			LocalDateTime start,

			LocalDateTime end

	);

}