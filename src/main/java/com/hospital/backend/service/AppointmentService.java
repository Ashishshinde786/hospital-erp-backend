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

/*
=========================================================
SERVICE: AppointmentService

Purpose:

BUSINESS LOGIC layer for appointments.

This is NOT controller.

This is NOT database repository.

This is BUSINESS RULE layer.

Responsible for:

- Create appointments

- Update appointments

- Change status

- Find appointments

- Validate patient exists

- Validate doctor exists

- Convert Entity <-> DTO

=========================================================
*/

@Service

/*
 * Marks as Spring service bean.
 * 
 * Can inject into controller.
 */
@RequiredArgsConstructor

/*
 * Lombok creates constructor:
 * 
 * AppointmentService( repository, patientRepository, doctorRepository )
 */
@Transactional

/*
 * Default:
 * 
 * all methods transactional.
 * 
 * If method changes database:
 * 
 * commit
 * 
 * or rollback automatically.
 */
public class AppointmentService {

	/*
	 * ===================================================== DATA ACCESS
	 * DEPENDENCIES =====================================================
	 */

	/*
	 * Appointment database operations
	 */
	private final AppointmentRepository appointmentRepository;

	/*
	 * Needed to verify patient exists.
	 */
	private final PatientRepository patientRepository;

	/*
	 * Needed to verify doctor exists.
	 */
	private final DoctorRepository doctorRepository;

	/*
	 * ===================================================== GET ALL APPOINTMENTS
	 * =====================================================
	 */
	@Transactional(readOnly = true)

	/*
	 * Optimization.
	 * 
	 * Read only transaction.
	 * 
	 * Faster.
	 * 
	 * No update tracking.
	 */
	public List<AppointmentDTO> getAll() {

		return appointmentRepository

				.findAll()

				.stream()

				/*
				 * Convert each entity to DTO
				 */
				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ===================================================== GET ONE APPOINTMENT BY
	 * ID =====================================================
	 */
	@Transactional(readOnly = true)

	public AppointmentDTO getById(Long id) {

		/*
		 * Find entity
		 * 
		 * Convert to DTO
		 */
		return toDTO(

				findById(id)

		);
	}

	/*
	 * ===================================================== GET APPOINTMENTS BY
	 * PATIENT =====================================================
	 */
	@Transactional(readOnly = true)

	public List<AppointmentDTO>

			getByPatient(Long patientId) {

		return appointmentRepository

				.findByPatientId(patientId)

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ===================================================== GET APPOINTMENTS BY
	 * DOCTOR =====================================================
	 */
	@Transactional(readOnly = true)

	public List<AppointmentDTO>

			getByDoctor(Long doctorId) {

		return appointmentRepository

				.findByDoctorId(doctorId)

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ===================================================== FIND BY DATE RANGE
	 * =====================================================
	 */
	@Transactional(readOnly = true)

	public List<AppointmentDTO>

			getByDateRange(

					LocalDateTime start,

					LocalDateTime end

	) {

		return appointmentRepository

				.findByDateRange(start, end)

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ===================================================== CREATE APPOINTMENT
	 * 
	 * IMPORTANT BUSINESS LOGIC
	 * =====================================================
	 */
	public AppointmentDTO create(AppointmentDTO dto) {

		/*
		 * Step 1
		 * 
		 * Validate patient exists.
		 */
		Patient patient =

				patientRepository

						.findById(dto.getPatientId())

						.orElseThrow(

								() -> new ResourceNotFoundException(

										"Patient not found"

								)

						);

		/*
		 * Step 2
		 * 
		 * Validate doctor exists.
		 */
		Doctor doctor =

				doctorRepository

						.findById(dto.getDoctorId())

						.orElseThrow(

								() -> new ResourceNotFoundException(

										"Doctor not found"

								)

						);

		/*
		 * Step 3
		 * 
		 * Create Appointment entity
		 */
		Appointment appointment =

				Appointment.builder()

						.patient(patient)

						.doctor(doctor)

						.appointmentDateTime(dto.getAppointmentDateTime())

						.reason(dto.getReason())

						.notes(dto.getNotes())

						/*
						 * default status
						 */
						.status(AppointmentStatus.SCHEDULED)

						.build();

		/*
		 * Step 4
		 * 
		 * Save to database
		 */
		return toDTO(

				appointmentRepository.save(appointment)

		);
	}

	/*
	 * ===================================================== UPDATE STATUS
	 * 
	 * Example:
	 * 
	 * SCHEDULED
	 * 
	 * -> COMPLETED =====================================================
	 */
	public AppointmentDTO updateStatus(

			Long id,

			AppointmentStatus status

	) {

		Appointment appointment =

				findById(id);

		appointment.setStatus(status);

		return toDTO(

				appointmentRepository.save(appointment)

		);
	}

	/*
	 * ===================================================== UPDATE APPOINTMENT
	 * =====================================================
	 */
	public AppointmentDTO update(

			Long id,

			AppointmentDTO dto

	) {

		Appointment existing = findById(id);

		existing.setAppointmentDateTime(dto.getAppointmentDateTime());

		existing.setReason(dto.getReason());

		existing.setNotes(dto.getNotes());

		/*
		 * Only update if status provided.
		 */
		if (dto.getStatus() != null) {

			existing.setStatus(dto.getStatus());
		}

		return toDTO(

				appointmentRepository.save(existing)

		);
	}

	/*
	 * ===================================================== DELETE APPOINTMENT
	 * =====================================================
	 */
	public void delete(Long id) {

		appointmentRepository.delete(

				findById(id)

		);
	}

	/*
	 * ===================================================== COUNT TODAY'S
	 * APPOINTMENTS =====================================================
	 */
	public long getTodayCount() {

		/*
		 * start of today
		 * 
		 * 00:00
		 */
		LocalDateTime startOfDay =

				LocalDateTime.now()

						.toLocalDate()

						.atStartOfDay();

		/*
		 * tomorrow start
		 * 
		 * acts as day end boundary
		 */
		LocalDateTime endOfDay =

				startOfDay.plusDays(1);

		return appointmentRepository

				.findByDateRange(

						startOfDay,

						endOfDay

				)

				.size();

	}

	/*
	 * ===================================================== PRIVATE HELPER
	 * 
	 * reused in many methods =====================================================
	 */
	private Appointment findById(Long id) {

		return appointmentRepository

				.findById(id)

				.orElseThrow(

						() -> new ResourceNotFoundException(

								"Appointment not found with id: " + id

						)

				);
	}

	/*
	 * ===================================================== ENTITY -> DTO
	 * conversion
	 * 
	 * Very important.
	 * 
	 * Hides entity structure.
	 * 
	 * Returns API response object.
	 * =====================================================
	 */
	private AppointmentDTO toDTO(Appointment a) {

		return AppointmentDTO.builder()

				.id(a.getId())

				.patientId(a.getPatient().getId())

				.doctorId(a.getDoctor().getId())

				.appointmentDateTime(a.getAppointmentDateTime())

				.status(a.getStatus())

				.reason(a.getReason())

				.notes(a.getNotes())

				/*
				 * derived field
				 */
				.patientName(

						a.getPatient().getFirstName()

								+ " " +

								a.getPatient().getLastName()

				)

				.doctorName(

						a.getDoctor().getFirstName()

								+ " " +

								a.getDoctor().getLastName()

				)

				.doctorSpecialization(

						a.getDoctor().getSpecialization()

				)

				.build();
	}

}