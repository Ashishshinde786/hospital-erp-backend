package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.AppointmentDTO;
import com.hospital.backend.entity.Appointment.AppointmentStatus;
import com.hospital.backend.service.AppointmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

	private final AppointmentService appointmentService;

	// GET ALL
	@GetMapping
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAll() {
		log.info("API CALL: Get all appointments");
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getAll(), "Appointments fetched"));
	}

	// GET BY ID
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getById(@PathVariable Long id) {
		log.info("API CALL: Get appointment id={}", id);
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getById(id), "Appointment fetched"));
	}

	// CREATE
	@PostMapping
	public ResponseEntity<ApiResponse<AppointmentDTO>> create(@Valid @RequestBody AppointmentDTO dto) {
		log.info("API CALL: Create appointment patientId={} doctorId={}", dto.getPatientId(), dto.getDoctorId());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(appointmentService.create(dto), "Appointment created"));
	}

	// UPDATE
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> update(@PathVariable Long id,
			@Valid @RequestBody AppointmentDTO dto) {

		log.info("API CALL: Update appointment id={}", id);

		return ResponseEntity.ok(ApiResponse.success(appointmentService.update(id, dto), "Appointment updated"));
	}

	// UPDATE STATUS
	@PatchMapping("/{id}/status")
	public ResponseEntity<ApiResponse<AppointmentDTO>> updateStatus(@PathVariable Long id,
			@RequestParam AppointmentStatus status) {

		log.info("API CALL: Update appointment status id={} status={}", id, status);

		return ResponseEntity.ok(ApiResponse.success(appointmentService.updateStatus(id, status), "Status updated"));
	}

	// DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.warn("API CALL: Delete appointment id={}", id);

		appointmentService.delete(id);

		return ResponseEntity.ok(ApiResponse.success(null, "Appointment deleted"));
	}

	// BY PATIENT
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getByPatient(@PathVariable Long patientId) {
		log.info("API CALL: Get appointments by patientId={}", patientId);

		return ResponseEntity.ok(ApiResponse.success(appointmentService.getByPatient(patientId), "Fetched"));
	}

	// BY DOCTOR
	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getByDoctor(@PathVariable Long doctorId) {
		log.info("API CALL: Get appointments by doctorId={}", doctorId);

		return ResponseEntity.ok(ApiResponse.success(appointmentService.getByDoctor(doctorId), "Fetched"));
	}

	// DATE RANGE
	@GetMapping("/range")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getByRange(
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

		log.info("API CALL: Get appointments between {} and {}", start, end);

		return ResponseEntity.ok(ApiResponse.success(appointmentService.getByDateRange(start, end), "Fetched"));
	}

	// TODAY COUNT
	@GetMapping("/today-count")
	public ResponseEntity<ApiResponse<Long>> getTodayCount() {
		log.info("API CALL: Get today's appointment count");

		return ResponseEntity
				.ok(ApiResponse.success(appointmentService.getTodayCount(), "Today's appointment count fetched"));
	}
}