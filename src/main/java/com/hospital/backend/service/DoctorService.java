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

@Service
@RequiredArgsConstructor
@Transactional
public class DoctorService {

	private final DoctorRepository doctorRepository;
	private final UserRepository userRepository;

	// ✅ GET ALL DOCTORS
	@Transactional(readOnly = true)
	public List<DoctorDTO> getAllDoctors() {
		return doctorRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ GET DOCTOR BY ID
	@Transactional(readOnly = true)
	public DoctorDTO getDoctorById(Long id) {
		return toDTO(findDoctorById(id));
	}

	// ✅ GET AVAILABLE DOCTORS
	@Transactional(readOnly = true)
	public List<DoctorDTO> getAvailableDoctors() {
		return doctorRepository.findByAvailableTrue().stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ GET BY SPECIALIZATION
	@Transactional(readOnly = true)
	public List<DoctorDTO> getDoctorsBySpecialization(String specialization) {
		return doctorRepository.findBySpecializationIgnoreCase(specialization).stream().map(this::toDTO)
				.collect(Collectors.toList());
	}

	// ✅ CREATE DOCTOR
	public DoctorDTO createDoctor(DoctorDTO dto) {

		Doctor doctor = toEntity(dto);

		// Link doctor with user (optional)
		if (dto.getUserId() != null) {
			User user = userRepository.findById(dto.getUserId())
					.orElseThrow(() -> new ResourceNotFoundException("User not found"));
			doctor.setUser(user);
		}

		return toDTO(doctorRepository.save(doctor));
	}

	// ✅ UPDATE DOCTOR
	public DoctorDTO updateDoctor(Long id, DoctorDTO dto) {

		Doctor existing = findDoctorById(id);

		existing.setFirstName(dto.getFirstName());
		existing.setLastName(dto.getLastName());
		existing.setSpecialization(dto.getSpecialization());
		existing.setLicenseNumber(dto.getLicenseNumber());
		existing.setEmail(dto.getEmail());
		existing.setPhone(dto.getPhone());
		existing.setQualification(dto.getQualification());
		existing.setBio(dto.getBio());
		existing.setAvailable(dto.isAvailable());

		return toDTO(doctorRepository.save(existing));
	}

	// ✅ DELETE DOCTOR
	public void deleteDoctor(Long id) {
		doctorRepository.delete(findDoctorById(id));
	}

	// ✅ COUNT DOCTORS
	public long getTotalCount() {
		return doctorRepository.count();
	}

	// 🔥 NEW: FILTER BY AVAILABILITY
	@Transactional(readOnly = true)
	public List<DoctorDTO> getDoctorsByAvailability(boolean available) {

		return doctorRepository.findAll().stream().filter(d -> d.isAvailable() == available).map(this::toDTO)
				.collect(Collectors.toList());
	}

	// 🔥 NEW: UPDATE AVAILABILITY (VERY IMPORTANT IN REAL WORLD)
	public DoctorDTO updateAvailability(Long id, boolean available) {

		Doctor doctor = findDoctorById(id);
		doctor.setAvailable(available);

		return toDTO(doctorRepository.save(doctor));
	}

	// ✅ SEARCH DOCTORS BY NAME
	@Transactional(readOnly = true)
	public List<DoctorDTO> searchDoctorsByName(String name) {

		return doctorRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name).stream()
				.map(this::toDTO).collect(Collectors.toList());
	}

	// ================= HELPER METHODS =================

	private Doctor findDoctorById(Long id) {
		return doctorRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
	}

	private DoctorDTO toDTO(Doctor d) {
		return DoctorDTO.builder().id(d.getId()).firstName(d.getFirstName()).lastName(d.getLastName())
				.specialization(d.getSpecialization()).licenseNumber(d.getLicenseNumber()).email(d.getEmail())
				.phone(d.getPhone()).qualification(d.getQualification()).bio(d.getBio()).available(d.isAvailable())
				.userId(d.getUser() != null ? d.getUser().getId() : null).build();
	}

	private Doctor toEntity(DoctorDTO dto) {
		return Doctor.builder().firstName(dto.getFirstName()).lastName(dto.getLastName())
				.specialization(dto.getSpecialization()).licenseNumber(dto.getLicenseNumber()).email(dto.getEmail())
				.phone(dto.getPhone()).qualification(dto.getQualification()).bio(dto.getBio())
				.available(dto.isAvailable()).build();
	}
}