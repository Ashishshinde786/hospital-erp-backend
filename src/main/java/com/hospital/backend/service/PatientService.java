package com.hospital.backend.service;

import com.hospital.backend.dto.PatientDTO;
import com.hospital.backend.entity.Patient;
import com.hospital.backend.exception.ResourceNotFoundException;
import com.hospital.backend.repository.PatientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

	private final PatientRepository patientRepository;

	// ✅ GET ALL
	@Transactional(readOnly = true)
	public List<PatientDTO> getAllPatients() {
		log.info("Fetching all patients");
		return patientRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ GET BY ID
	@Transactional(readOnly = true)
	public PatientDTO getPatientById(Long id) {
		log.info("Fetching patient id: {}", id);
		return toDTO(findPatientById(id));
	}

	// ✅ CREATE
	public PatientDTO createPatient(PatientDTO dto) {
		log.info("Creating patient: {} {}", dto.getFirstName(), dto.getLastName());
		Patient saved = patientRepository.save(toEntity(dto));
		return toDTO(saved);
	}

	// ✅ UPDATE
	public PatientDTO updatePatient(Long id, PatientDTO dto) {
		log.info("Updating patient id: {}", id);

		Patient p = findPatientById(id);

		p.setFirstName(dto.getFirstName());
		p.setLastName(dto.getLastName());
		p.setDateOfBirth(dto.getDateOfBirth());
		p.setGender(dto.getGender());
		p.setPhone(dto.getPhone());
		p.setEmail(dto.getEmail());
		p.setAddress(dto.getAddress());
		p.setBloodGroup(dto.getBloodGroup());
		p.setMedicalHistory(dto.getMedicalHistory());

		return toDTO(patientRepository.save(p));
	}

	// ✅ DELETE
	public void deletePatient(Long id) {
		log.warn("Deleting patient id: {}", id);
		patientRepository.delete(findPatientById(id));
	}

	// ✅ SEARCH
	@Transactional(readOnly = true)
	public List<PatientDTO> searchPatients(String query) {
		log.info("Searching patients: {}", query);

		return patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
				.stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ COUNT
	@Transactional(readOnly = true)
	public long getTotalCount() {
		return patientRepository.count();
	}

	// ✅ BLOOD GROUP
	@Transactional(readOnly = true)
	public List<PatientDTO> getPatientsByBloodGroup(String group) {
		log.info("Fetching blood group: {}", group);

		return patientRepository.findByBloodGroup(group).stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ AGE RANGE
	@Transactional(readOnly = true)
	public List<PatientDTO> getPatientsByAgeRange(int min, int max) {

		if (min < 0 || max < 0 || min > max) {
			throw new IllegalArgumentException("Invalid age range");
		}

		log.info("Fetching patients age between {} and {}", min, max);

		LocalDate today = LocalDate.now();
		LocalDate maxDate = today.minusYears(min);
		LocalDate minDate = today.minusYears(max);

		return patientRepository.findAll().stream().filter(p -> p.getDateOfBirth() != null)
				.filter(p -> !p.getDateOfBirth().isAfter(maxDate) && !p.getDateOfBirth().isBefore(minDate))
				.map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ RECENT PATIENTS (FIXED)
	@Transactional(readOnly = true)
	public List<PatientDTO> getRecentPatients(int days) {

		if (days <= 0) {
			throw new IllegalArgumentException("Days must be positive");
		}

		log.info("Fetching patients created in last {} days", days);

		LocalDateTime cutoff = LocalDateTime.now().minusDays(days);

		return patientRepository.findByCreatedAtAfter(cutoff).stream().map(this::toDTO).collect(Collectors.toList());
	}

	// 🔧 HELPER
	private Patient findPatientById(Long id) {
		return patientRepository.findById(id).orElseThrow(() -> {
			log.error("Patient not found: {}", id);
			return new ResourceNotFoundException("Patient not found with id: " + id);
		});
	}

	private PatientDTO toDTO(Patient p) {
		return PatientDTO.builder().id(p.getId()).firstName(p.getFirstName()).lastName(p.getLastName())
				.dateOfBirth(p.getDateOfBirth()).gender(p.getGender()).phone(p.getPhone()).email(p.getEmail())
				.address(p.getAddress()).bloodGroup(p.getBloodGroup()).medicalHistory(p.getMedicalHistory()).build();
	}

	private Patient toEntity(PatientDTO dto) {
		return Patient.builder().firstName(dto.getFirstName()).lastName(dto.getLastName())
				.dateOfBirth(dto.getDateOfBirth()).gender(dto.getGender()).phone(dto.getPhone()).email(dto.getEmail())
				.address(dto.getAddress()).bloodGroup(dto.getBloodGroup()).medicalHistory(dto.getMedicalHistory())
				.build();
	}
}
