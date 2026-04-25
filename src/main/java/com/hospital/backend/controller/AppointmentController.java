package com.hospital.backend.controller;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This is REST Controller for appointments.

It exposes API endpoints for:

Create appointment

Read appointment

Update appointment

Delete appointment

Search by patient

Search by doctor

Search by date range

Update status

---------------------------------------------------------

This class handles HTTP requests.

It DOES NOT contain business logic.

It delegates business logic to:

AppointmentService

Very important separation.

Controller:

Handles requests.

Service:

Handles business logic.

Repository:

Handles database.

Classic layered architecture.
=========================================================
*/

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.AppointmentDTO;

import com.hospital.backend.entity.Appointment.AppointmentStatus;

import com.hospital.backend.service.AppointmentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/*
@RestController

Marks this class as REST API controller.

Spring detects it.

Registers endpoints.

Combines:

@Controller
+

@ResponseBody

Means:

Return JSON automatically.
*/
@RestController

/*
 * Base URL for all endpoints.
 * 
 * Everything starts with:
 * 
 * /api/appointments
 */
@RequestMapping("/api/appointments")

/*
 * Generates constructor
 * 
 * injects AppointmentService
 */
@RequiredArgsConstructor
public class AppointmentController {

	/*
	 * Controller depends on Service.
	 * 
	 * relationship:
	 * 
	 * Controller
	 * 
	 * -> Service
	 * 
	 * -> Repository
	 * 
	 * -> Database
	 */
	private final AppointmentService appointmentService;

	/*
	 * GET
	 * 
	 * /api/appointments
	 * 
	 * Fetch all appointments
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getAll() {
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getAll(), "Appointments fetched"));
	}

	/*
	 * GET
	 * 
	 * /api/appointments/5
	 * 
	 * fetch appointment by id
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> getById(
			/*
			 * Takes value from URL.
			 * 
			 * /5
			 * 
			 * becomes:
			 * 
			 * id=5
			 */
			@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getById(id), "Appointment fetched"));
	}

	/*
	 * POST
	 * 
	 * Create new appointment
	 * 
	 * client sends JSON body
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<AppointmentDTO>> create(
			/*
			 * Read JSON body
			 * 
			 * convert into DTO object.
			 */
			@RequestBody
			/*
			 * validate DTO fields
			 * 
			 * NotNull etc.
			 */
			@Valid AppointmentDTO dto) {
		return ResponseEntity
				/*
				 * HTTP 201 Created
				 */
				.status(HttpStatus.CREATED)
				.body(ApiResponse.success(appointmentService.create(dto), "Appointment created successfully"

				));
	}

	/*
	 * PUT
	 * 
	 * full update
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<AppointmentDTO>> update(@PathVariable Long id,
			@RequestBody @Valid AppointmentDTO dto

	) {
		return ResponseEntity.ok(ApiResponse.success(appointmentService.update(id, dto), "Appointment updated"));
	}

	/*
	 * PATCH
	 * 
	 * Partial update
	 * 
	 * only status.
	 */
	@PatchMapping("/{id}/status")
	public ResponseEntity<ApiResponse<AppointmentDTO>> updateStatus(@PathVariable Long id,

			/*
			 * Query param:
			 * 
			 * ?status=COMPLETED
			 */
			@RequestParam AppointmentStatus status

	) {
		return ResponseEntity.ok(ApiResponse.success(appointmentService.updateStatus(id, status), "Status updated"));
	}

	/*
	 * DELETE appointment
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id

	) {
		appointmentService.delete(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Appointment deleted"));
	}

	/*
	 * Get appointments by patient.
	 * 
	 * Example:
	 * 
	 * /patient/3
	 */
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getByPatient(@PathVariable Long patientId

	) {
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getByPatient(patientId), "Fetched"));
	}

	/*
	 * Get appointments by doctor
	 */
	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getByDoctor(@PathVariable Long doctorId) {
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getByDoctor(doctorId), "Fetched"));
	}

	/*
	 * Search by date range.
	 * 
	 * Example:
	 * 
	 * /range?
	 * 
	 * start=2026-04-21T10:00:00
	 * 
	 * &
	 * 
	 * end=2026-04-22T10:00:00
	 */
	@GetMapping("/range")
	public ResponseEntity<ApiResponse<List<AppointmentDTO>>> getByRange(@RequestParam
	/*
	 * Convert request string
	 * 
	 * into LocalDateTime.
	 */
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end

	) {
		return ResponseEntity.ok(ApiResponse.success(appointmentService.getByDateRange(start, end), "Fetched"));
	}
	
	
	/*
	 * GET today's appointment count
	 * 
	 * Dashboard API
	 * 
	 * Example:
	 * 
	 * /api/appointments/today-count
	 */
	@GetMapping("/today-count")
	public ResponseEntity<ApiResponse<Long>> getTodayCount() {

	    return ResponseEntity.ok(
	            ApiResponse.success(
	                    appointmentService.getTodayCount(),
	                    "Today's appointment count fetched"
	            )
	    );
	}

}