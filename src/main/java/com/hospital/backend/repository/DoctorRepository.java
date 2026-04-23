package com.hospital.backend.repository;

import com.hospital.backend.entity.Doctor;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

/*
=========================================================
REPOSITORY: DoctorRepository

Purpose:

Database access layer for Doctor entity.

Responsible for:

- Saving doctors

- Finding doctors

- Searching doctors

- Checking doctor availability

- Checking duplicate license numbers

This is DATA ACCESS logic.

NOT business logic.

Works with:

Doctor entity

Primary key:

Long

=========================================================
*/

@Repository

/*
 * Spring creates bean automatically.
 * 
 * Can inject into service:
 * 
 * private final DoctorRepository doctorRepository;
 */
public interface DoctorRepository

		/*
		 * Extending JpaRepository gives built-in methods:
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
		 * and many more.
		 */
		extends JpaRepository<Doctor, Long> {

	/*
	 * ===================================================== DERIVED QUERY
	 * 
	 * Search doctors by specialization.
	 * 
	 * Example:
	 * 
	 * "Cardiology"
	 * 
	 * "Neurology"
	 * 
	 * 
	 * 
	 * IgnoreCase means:
	 * 
	 * cardiology
	 * 
	 * CARDIOLOGY
	 * 
	 * Cardiology
	 * 
	 * all work.
	 * 
	 * Spring auto generates query.
	 * 
	 * SQL idea:
	 * 
	 * SELECT * FROM doctors WHERE lower(specialization)=lower(?)
	 * 
	 * =====================================================
	 */
	List<Doctor>

			findBySpecializationIgnoreCase(

					String specialization

	);

	/*
	 * ===================================================== Find ONLY available
	 * doctors.
	 * 
	 * "Available" means:
	 * 
	 * available = true
	 * 
	 * Spring understands:
	 * 
	 * findByAvailableTrue()
	 * 
	 * and auto builds query.
	 * 
	 * SQL idea:
	 * 
	 * SELECT * FROM doctors WHERE available=true
	 * 
	 * Used for:
	 * 
	 * appointment booking
	 * 
	 * doctor selection
	 * 
	 * scheduling =====================================================
	 */
	List<Doctor>

			findByAvailableTrue();

	/*
	 * ===================================================== EXISTS CHECK
	 * 
	 * Returns:
	 * 
	 * true
	 * 
	 * or
	 * 
	 * false
	 * 
	 * Used to prevent duplicate license numbers.
	 * 
	 * Example:
	 * 
	 * license:
	 * 
	 * MED12345
	 * 
	 * already exists?
	 * 
	 * true -> reject insert
	 * 
	 * false -> allow insert
	 * 
	 * SQL idea:
	 * 
	 * SELECT EXISTS(...)
	 * 
	 * Very useful validation query.
	 * =====================================================
	 */
	boolean existsByLicenseNumber(

			String licenseNumber

	);

	/*
	 * ========================================== SEARCH BY FIRST NAME OR LAST NAME
	 * ==========================================
	 */
	List<Doctor> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);

}