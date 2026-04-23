package com.hospital.backend.service;

import com.hospital.backend.dto.DoctorDTO;

import com.hospital.backend.entity.Doctor;
import com.hospital.backend.entity.User;

import com.hospital.backend.exception.ResourceNotFoundException;

import com.hospital.backend.repository.DoctorRepository;
import com.hospital.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.util.stream.Collectors;

/*
=======================================================

DOCTOR SERVICE

Business Logic Layer

Handles:

1 Create doctor

2 Update doctor

3 Delete doctor

4 Find doctor

5 Search by specialization

6 Find available doctors

7 Link doctor to user login account

=======================================================
*/

@Service

/*
 * Spring creates object automatically
 * 
 * Inject into controller.
 */
@RequiredArgsConstructor

/*
 * Creates constructor automatically for final dependencies.
 */
@Transactional

/*
 * Default transaction for all methods.
 * 
 * If DB operation fails
 * 
 * rollback.
 */
public class DoctorService {

	/*
	 * ========================================== DEPENDENCIES
	 * ==========================================
	 */

	/*
	 * Handles doctor table.
	 */
	private final DoctorRepository doctorRepository;

	/*
	 * Needed when linking doctor with system user account.
	 */
	private final UserRepository userRepository;

	/*
	 * ========================================== GET ALL DOCTORS
	 * ==========================================
	 */
	@Transactional(readOnly = true)

	/*
	 * readOnly improves performance for select operations.
	 */
	public List<DoctorDTO> getAllDoctors() {

		return doctorRepository

				.findAll()

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ========================================== GET SINGLE DOCTOR
	 * ==========================================
	 */
	@Transactional(readOnly = true)

	public DoctorDTO getDoctorById(Long id) {

		return toDTO(

				findDoctorById(id)

		);
	}

	/*
	 * ========================================== GET AVAILABLE DOCTORS
	 * 
	 * available=true ==========================================
	 */
	@Transactional(readOnly = true)

	public List<DoctorDTO> getAvailableDoctors() {

		return doctorRepository

				.findByAvailableTrue()

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ========================================== SEARCH BY SPECIALIZATION
	 * 
	 * Example:
	 * 
	 * CARDIOLOGY
	 * 
	 * DERMATOLOGY
	 * 
	 * NEUROLOGY ==========================================
	 */
	@Transactional(readOnly = true)

	public List<DoctorDTO>

			getDoctorsBySpecialization(

					String specialization

	) {

		return doctorRepository

				.findBySpecializationIgnoreCase(

						specialization

				)

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ========================================== CREATE DOCTOR
	 * ==========================================
	 * 
	 * Steps
	 * 
	 * 1 convert DTO -> Entity
	 * 
	 * 2 optionally link User
	 * 
	 * 3 save doctor
	 */
	public DoctorDTO createDoctor(

			DoctorDTO dto

	) {

		/*
		 * STEP 1
		 * 
		 * Convert DTO into entity.
		 */
		Doctor doctor =

				toEntity(dto);

		/*
		 * STEP 2
		 * 
		 * Optional link:
		 * 
		 * doctor -> user login
		 * 
		 * Example
		 * 
		 * Doctor:
		 * 
		 * Dr Smith
		 * 
		 * linked to:
		 * 
		 * username=drsmith
		 */
		if (

		dto.getUserId() != null

		) {

			User user =

					userRepository

							.findById(

									dto.getUserId()

							)

							.orElseThrow(

									() -> new ResourceNotFoundException(

											"User not found"

									));

			/*
			 * set relationship
			 * 
			 * doctor belongs to user
			 */
			doctor.setUser(user);

		}

		/*
		 * STEP 3
		 * 
		 * Save doctor
		 */
		return toDTO(

				doctorRepository.save(

						doctor

				)

		);

	}

	/*
	 * ========================================== UPDATE DOCTOR
	 * ==========================================
	 */
	public DoctorDTO updateDoctor(

			Long id,

			DoctorDTO dto

	) {

		/*
		 * find existing doctor
		 */
		Doctor existing =

				findDoctorById(id);

		/*
		 * update fields
		 */
		existing.setFirstName(

				dto.getFirstName()

		);

		existing.setLastName(

				dto.getLastName()

		);

		existing.setSpecialization(

				dto.getSpecialization()

		);

		existing.setLicenseNumber(

				dto.getLicenseNumber()

		);

		existing.setEmail(

				dto.getEmail()

		);

		existing.setPhone(

				dto.getPhone()

		);

		existing.setQualification(

				dto.getQualification()

		);

		existing.setBio(

				dto.getBio()

		);

		existing.setAvailable(

				dto.isAvailable()

		);

		/*
		 * save updated doctor
		 */
		return toDTO(

				doctorRepository.save(

						existing

				));
	}

	/*
	 * ========================================== DELETE DOCTOR
	 * ==========================================
	 */
	public void deleteDoctor(

			Long id) {

		doctorRepository.delete(

				findDoctorById(id)

		);

	}

	/*
	 * ========================================== COUNT DOCTORS
	 * ==========================================
	 */
	public long getTotalCount() {

		return doctorRepository.count();
	}

	/*
	 * ========================================== HELPER
	 * 
	 * FIND DOCTOR ==========================================
	 */
	private Doctor findDoctorById(

			Long id

	) {

		return doctorRepository

				.findById(id)

				.orElseThrow(

						() -> new ResourceNotFoundException(

								"Doctor not found with id:" + id

						));
	}

	/*
	 * ========================================== ENTITY -> DTO
	 * 
	 * Database -> API response ==========================================
	 */
	private DoctorDTO toDTO(

			Doctor d

	) {

		return DoctorDTO.builder()

				.id(d.getId())

				.firstName(d.getFirstName())

				.lastName(d.getLastName())

				.specialization(d.getSpecialization())

				.licenseNumber(d.getLicenseNumber())

				.email(d.getEmail())

				.phone(d.getPhone())

				.qualification(d.getQualification())

				.bio(d.getBio())

				.available(d.isAvailable())

				/*
				 * return linked user id
				 */
				.userId(

						d.getUser() != null

								?

								d.getUser().getId()

								:

								null)

				.build();
	}

	/*
	 * ========================================== DTO -> ENTITY
	 * 
	 * Request -> database object ==========================================
	 */
	private Doctor toEntity(

			DoctorDTO dto

	) {

		return Doctor.builder()

				.firstName(dto.getFirstName())

				.lastName(dto.getLastName())

				.specialization(dto.getSpecialization())

				.licenseNumber(dto.getLicenseNumber())

				.email(dto.getEmail())

				.phone(dto.getPhone())

				.qualification(dto.getQualification())

				.bio(dto.getBio())

				.available(dto.isAvailable())

				.build();
	}

	/*
	 * ========================================== SEARCH DOCTORS BY NAME
	 * ==========================================
	 */
	@Transactional(readOnly = true)
	public List<DoctorDTO> searchDoctorsByName(String name) {

		return doctorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name).stream()
				.map(this::toDTO).collect(Collectors.toList());
	}

}