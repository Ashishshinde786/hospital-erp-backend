package com.hospital.backend.repository;

import com.hospital.backend.entity.Patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;

import java.util.List;

/*
=========================================================
REPOSITORY: PatientRepository

Purpose:

Database access layer for Patient module.

Handles:

- Save patients

- Find patients

- Search patients

- Search by blood group

- Count patients

Works with:

Patient entity

Primary key:

Long

=========================================================
*/

@Repository

public interface PatientRepository

		extends JpaRepository<Patient, Long> {

	/*
	 * ===================================================== DERIVED QUERY
	 * 
	 * Search patients by name.
	 * 
	 * Very long method name,
	 * 
	 * but Spring understands it.
	 * 
	 * Breakdown:
	 * 
	 * findBy
	 * 
	 * FirstNameContainingIgnoreCase
	 * 
	 * OR
	 * 
	 * LastNameContainingIgnoreCase
	 * 
	 * Meaning:
	 * 
	 * search in first name OR last name
	 * 
	 * partial matching.
	 * 
	 * Example:
	 * 
	 * query:
	 * 
	 * "ash"
	 * 
	 * finds:
	 * 
	 * Ashish
	 * 
	 * Ashok
	 * 
	 * or
	 * 
	 * Shinde Ash
	 * 
	 * IgnoreCase:
	 * 
	 * ash
	 * 
	 * ASH
	 * 
	 * Ash
	 * 
	 * all work. =====================================================
	 */
	List<Patient>

			findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(

					String firstName,

					String lastName

	);

	/*
	 * ===================================================== CUSTOM JPQL QUERY
	 * 
	 * Find patients by blood group.
	 * 
	 * Example:
	 * 
	 * O+
	 * 
	 * A-
	 * 
	 * Used for:
	 * 
	 * blood donation
	 * 
	 * emergency matching
	 * 
	 * hospital filtering =====================================================
	 */
	@Query(

	"SELECT p FROM Patient p " +

			"WHERE p.bloodGroup=:bloodGroup"

	)

	List<Patient>

			findByBloodGroup(

					String bloodGroup

	);

	/*
	 * ===================================================== COUNT ALL PATIENTS
	 * 
	 * Returns total number.
	 * 
	 * Used for:
	 * 
	 * Dashboard
	 * 
	 * Analytics
	 * 
	 * Reports
	 * 
	 * Example:
	 * 
	 * Total registered patients:
	 * 
	 * 12,540 =====================================================
	 */
	@Query(

	"SELECT COUNT(p) FROM Patient p"

	)

	long countAllPatients();

}