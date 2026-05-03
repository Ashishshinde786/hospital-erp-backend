package com.hospital.backend.service;

import com.hospital.backend.dto.AppointmentDTO;
import com.hospital.backend.entity.*;
import com.hospital.backend.entity.Appointment.AppointmentStatus;
import com.hospital.backend.exception.ResourceNotFoundException;
import com.hospital.backend.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService {

	private final AppointmentRepository appointmentRepository;
	private final PatientRepository patientRepository;
	private final DoctorRepository doctorRepository;

	// ✅ GET ALL
	@Transactional(readOnly = true)
	public List<AppointmentDTO> getAll() {
		return appointmentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ GET BY ID
	@Transactional(readOnly = true)
	public AppointmentDTO getById(Long id) {
		return toDTO(findById(id));
	}

	// ✅ GET BY PATIENT
	@Transactional(readOnly = true)
	public List<AppointmentDTO> getByPatient(Long patientId) {
		return appointmentRepository.findByPatientId(patientId).stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ GET BY DOCTOR
	@Transactional(readOnly = true)
	public List<AppointmentDTO> getByDoctor(Long doctorId) {
		return appointmentRepository.findByDoctorId(doctorId).stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ GET BY DATE RANGE
	@Transactional(readOnly = true)
	public List<AppointmentDTO> getByDateRange(LocalDateTime start, LocalDateTime end) {
		return appointmentRepository.findByDateRange(start, end).stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ CREATE
	public AppointmentDTO create(AppointmentDTO dto) {

		// Validate patient
		Patient patient = patientRepository.findById(dto.getPatientId())
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

		// Validate doctor
		Doctor doctor = doctorRepository.findById(dto.getDoctorId())
				.orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

		// Create entity
		Appointment appointment = Appointment.builder().patient(patient).doctor(doctor)
				.appointmentDateTime(dto.getAppointmentDateTime()).reason(dto.getReason()).notes(dto.getNotes())
				.status(AppointmentStatus.SCHEDULED).build();

		return toDTO(appointmentRepository.save(appointment));
	}

	// ✅ UPDATE STATUS
	public AppointmentDTO updateStatus(Long id, AppointmentStatus status) {
		Appointment appointment = findById(id);
		appointment.setStatus(status);
		return toDTO(appointmentRepository.save(appointment));
	}

	// ✅ UPDATE FULL
	public AppointmentDTO update(Long id, AppointmentDTO dto) {

		Appointment existing = findById(id);

		existing.setAppointmentDateTime(dto.getAppointmentDateTime());
		existing.setReason(dto.getReason());
		existing.setNotes(dto.getNotes());

		if (dto.getStatus() != null) {
			existing.setStatus(dto.getStatus());
		}

		return toDTO(appointmentRepository.save(existing));
	}

	// ✅ DELETE
	public void delete(Long id) {
		appointmentRepository.delete(findById(id));
	}

	// ✅ TODAY COUNT
	public long getTodayCount() {

		LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
		LocalDateTime end = start.plusDays(1);

		return appointmentRepository.findByDateRange(start, end).size();
	}

	// 🔥 HELPER
	private Appointment findById(Long id) {
		return appointmentRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
	}

	// 🔥 ENTITY → DTO
	private AppointmentDTO toDTO(Appointment a) {
		return AppointmentDTO.builder().id(a.getId()).patientId(a.getPatient().getId()).doctorId(a.getDoctor().getId())
				.appointmentDateTime(a.getAppointmentDateTime()).status(a.getStatus()).reason(a.getReason())
				.notes(a.getNotes()).patientName(a.getPatient().getFirstName() + " " + a.getPatient().getLastName())
				.doctorName(a.getDoctor().getFirstName() + " " + a.getDoctor().getLastName())
				.doctorSpecialization(a.getDoctor().getSpecialization()).build();
	}
}