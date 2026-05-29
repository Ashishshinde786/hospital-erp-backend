package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.AppointmentDTO;
import com.hospital.backend.entity.Appointment.AppointmentStatus;
import com.hospital.backend.service.AppointmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Appointments", description = "Appointment scheduling — create, update, cancel, date range queries")
public class AppointmentController {

	private final AppointmentService appointmentService;

	@Operation(summary = "Get all appointments")
	@GetMapping
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAll() {
		log.info("API CALL: Get all appointments");
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getAll(), "Appointments fetched"));
	}

	@Operation(summary = "Get appointment by ID")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getById(
			@Parameter(description = "Appointment ID", example = "1") @PathVariable Long id) {
		log.info("API CALL: Get appointment id={}", id);
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getById(id), "Appointment fetched"));
	}

	@Operation(summary = "Create appointment", description = "Book a new appointment for a patient with a doctor")
	@PostMapping
	public ResponseEntity<ApiResponse<AppointmentDTO>> create(@Valid @RequestBody AppointmentDTO dto) {
		log.info("API CALL: Create appointment patientId={} doctorId={}", dto.getPatientId(), dto.getDoctorId());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(appointmentService.create(dto), "Appointment created"));
	}

	@Operation(summary = "Update appointment", description = "Update date/time, reason, notes, or status of an appointment")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> update(
			@Parameter(description = "Appointment ID") @PathVariable Long id, @Valid @RequestBody AppointmentDTO dto) {
		log.info("API CALL: Update appointment id={}", id);
		return ResponseEntity.ok(ApiResponse.success(appointmentService.update(id, dto), "Appointment updated"));
	}

	@Operation(summary = "Update appointment status", description = "Change status: SCHEDULED → CONFIRMED → COMPLETED / CANCELLED / NO_SHOW")
	@PatchMapping("/{id}/status")
	public ResponseEntity<ApiResponse<AppointmentDTO>> updateStatus(
			@Parameter(description = "Appointment ID") @PathVariable Long id,
			@Parameter(description = "New status", example = "CONFIRMED") @RequestParam AppointmentStatus status) {
		log.info("API CALL: Update appointment status id={} status={}", id, status);
		return ResponseEntity.ok(ApiResponse.success(appointmentService.updateStatus(id, status), "Status updated"));
	}

	@Operation(summary = "Delete appointment")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@Parameter(description = "Appointment ID") @PathVariable Long id) {
		log.warn("API CALL: Delete appointment id={}", id);
		appointmentService.delete(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Appointment deleted"));
	}

	@Operation(summary = "Get appointments by patient")
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getByPatient(
			@Parameter(description = "Patient ID", example = "1") @PathVariable Long patientId) {
		log.info("API CALL: Get appointments by patientId={}", patientId);
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getByPatient(patientId), "Fetched"));
	}

	@Operation(summary = "Get appointments by doctor")
	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getByDoctor(
			@Parameter(description = "Doctor ID", example = "1") @PathVariable Long doctorId) {
		log.info("API CALL: Get appointments by doctorId={}", doctorId);
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getByDoctor(doctorId), "Fetched"));
	}

	@Operation(summary = "Get appointments by date range", description = "Format: ISO 8601 — e.g. `2024-01-01T00:00:00` to `2024-01-31T23:59:59`")
	@GetMapping("/range")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getByRange(
			@Parameter(description = "Start datetime", example = "2024-01-01T00:00:00") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
			@Parameter(description = "End datetime", example = "2024-12-31T23:59:59") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
		log.info("API CALL: Get appointments between {} and {}", start, end);
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getByDateRange(start, end), "Fetched"));
	}

	@Operation(summary = "Get today's appointment count", description = "Returns count of appointments scheduled for today")
	@GetMapping("/today-count")
	public ResponseEntity<ApiResponse<Long>> getTodayCount() {
		log.info("API CALL: Get today's appointment count");
		return ResponseEntity
				.ok(ApiResponse.success(appointmentService.getTodayCount(), "Today's appointment count fetched"));
	}
}