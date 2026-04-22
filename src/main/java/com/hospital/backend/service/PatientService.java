package com.hospital.backend.service;

import com.hospital.backend.dto.PatientDTO;

import com.hospital.backend.entity.Patient;

import com.hospital.backend.exception.ResourceNotFoundException;

import com.hospital.backend.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.stream.Collectors;

/*
====================================================

PATIENT SERVICE

Business Logic Layer

Responsibilities:

Create patient

Update patient

Delete patient

Search patients

Fetch patients

Convert DTO <-> Entity

====================================================
*/

@Service
/*
 * Marks this as Spring Service Bean.
 * 
 * Spring creates object automatically.
 * 
 * Can be injected into Controller.
 */
@RequiredArgsConstructor
/*
 * Lombok creates constructor for:
 * 
 * private final PatientRepository patientRepository;
 */
@Transactional
/*
 * Default:
 * 
 * all methods run inside database transaction.
 * 
 * If something fails:
 * 
 * rollback.
 */
public class PatientService {

	/*
	 * Inject repository.
	 * 
	 * Service talks to database through repository.
	 * 
	 * NOT directly using SQL.
	 */
	private final PatientRepository patientRepository;

	/*
	 * ========================================== GET ALL PATIENTS
	 * ==========================================
	 * 
	 * Fetch all rows from patients table.
	 */
	@Transactional(readOnly = true)
	/*
	 * Read-only optimization.
	 * 
	 * Faster.
	 * 
	 * Prevents accidental update.
	 */
	public List<PatientDTO> getAllPatients() {

		return patientRepository.findAll()

				/*
				 * returns entities
				 */

				.stream()

				/*
				 * convert each entity -> DTO
				 */
				.map(this::toDTO)

				/*
				 * convert stream -> List
				 */
				.collect(Collectors.toList());
	}

	/*
	 * ========================================== GET ONE PATIENT BY ID
	 * ==========================================
	 */
	@Transactional(readOnly = true)
	public PatientDTO getPatientById(Long id) {

		/*
		 * first find entity
		 * 
		 * then convert to DTO
		 */
		return toDTO(

				findPatientById(id)

		);
	}

	/*
	 * ========================================== CREATE NEW PATIENT
	 * ==========================================
	 */
	public PatientDTO createPatient(

			PatientDTO dto

	) {

		/*
		 * Convert incoming API request
		 * 
		 * DTO -> Entity
		 */
		Patient patient =

				toEntity(dto);

		/*
		 * Save into database
		 */
		Patient saved =

				patientRepository.save(patient);

		/*
		 * return response as DTO
		 */
		return toDTO(saved);
	}

	/*
	 * ========================================== UPDATE PATIENT
	 * ==========================================
	 */
	public PatientDTO updatePatient(

			Long id,

			PatientDTO dto

	) {

		/*
		 * Find existing patient
		 */
		Patient existing =

				findPatientById(id);

		/*
		 * Update fields
		 */
		existing.setFirstName(

				dto.getFirstName()

		);

		existing.setLastName(

				dto.getLastName()

		);

		existing.setDateOfBirth(

				dto.getDateOfBirth()

		);

		existing.setGender(

				dto.getGender()

		);

		existing.setPhone(

				dto.getPhone()

		);

		existing.setEmail(

				dto.getEmail()

		);

		existing.setAddress(

				dto.getAddress()

		);

		existing.setBloodGroup(

				dto.getBloodGroup()

		);

		existing.setMedicalHistory(

				dto.getMedicalHistory()

		);

		/*
		 * Save updated entity
		 */
		return toDTO(

				patientRepository.save(existing)

		);
	}

	/*
	 * ========================================== DELETE PATIENT
	 * ==========================================
	 */
	public void deletePatient(Long id) {

		/*
		 * First verify patient exists
		 */
		Patient patient =

				findPatientById(id);

		/*
		 * Delete row
		 */
		patientRepository.delete(patient);
	}

	/*
	 * ========================================== SEARCH PATIENTS
	 * ==========================================
	 * 
	 * Search by:
	 * 
	 * firstName
	 * 
	 * OR
	 * 
	 * lastName
	 */
	@Transactional(readOnly = true)
	public List<PatientDTO> searchPatients(

			String query

	) {

		return patientRepository

				.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(

						query,

						query

				)

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ========================================== COUNT PATIENTS
	 * ==========================================
	 */
	@Transactional(readOnly = true)
	public long getTotalCount() {

		return patientRepository.count();

	}

	/*
	 * ========================================== HELPER METHOD
	 * 
	 * FIND PATIENT OR THROW EXCEPTION ==========================================
	 */
	private Patient findPatientById(Long id) {

		return patientRepository.findById(id)

				.orElseThrow(

						() ->

						new ResourceNotFoundException(

								"Patient not found with id: " + id

						)

				);
	}

	/*
	 * ========================================== ENTITY -> DTO
	 * 
	 * Database -> API Response ==========================================
	 */
	private PatientDTO toDTO(

			Patient p

	) {

		return PatientDTO.builder()

				.id(

						p.getId()

				)

				.firstName(

						p.getFirstName()

				)

				.lastName(

						p.getLastName()

				)

				.dateOfBirth(

						p.getDateOfBirth()

				)

				.gender(

						p.getGender()

				)

				.phone(

						p.getPhone()

				)

				.email(

						p.getEmail()

				)

				.address(

						p.getAddress()

				)

				.bloodGroup(

						p.getBloodGroup()

				)

				.medicalHistory(

						p.getMedicalHistory()

				)

				.build();
	}

	/*
	 * ========================================== DTO -> ENTITY
	 * 
	 * API Request -> Database Object ==========================================
	 */
	private Patient toEntity(

			PatientDTO dto

	) {

		return Patient.builder()

				.firstName(dto.getFirstName())

				.lastName(dto.getLastName())

				.dateOfBirth(dto.getDateOfBirth())

				.gender(dto.getGender())

				.phone(dto.getPhone())

				.email(dto.getEmail())

				.address(dto.getAddress())

				.bloodGroup(dto.getBloodGroup())

				.medicalHistory(dto.getMedicalHistory())

				.build();
	}

	/*
	 * ========================================== GET PATIENTS BY BLOOD GROUP
	 * ==========================================
	 */
	@Transactional(readOnly = true)
	public List<PatientDTO> getPatientsByBloodGroup(String bloodGroup) {

		return patientRepository.findByBloodGroup(bloodGroup).stream().map(this::toDTO).collect(Collectors.toList());
	}

}