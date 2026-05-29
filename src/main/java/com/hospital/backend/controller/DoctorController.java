package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.DoctorDTO;
import com.hospital.backend.service.DoctorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "Doctors", description = "Doctor master data — CRUD, availability, specialization search")
public class DoctorController {

	private final DoctorService doctorService;

	@Operation(summary = "Get all doctors")
	@GetMapping
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAll() {
		log.info("API CALL: Get all doctors");
		return ResponseEntity.ok(ApiResponse.success(doctorService.getAllDoctors(), "Doctors fetched successfully"));
	}

	@Operation(summary = "Get doctor by ID")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Doctor found"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Doctor not found") })
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<DoctorDTO>> getById(
			@Parameter(description = "Doctor ID", example = "1") @PathVariable Long id) {
		log.info("API CALL: Get doctor by id {}", id);
		return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorById(id), "Doctor fetched successfully"));
	}

	@Operation(summary = "Get available doctors", description = "Returns only doctors with available=true")
	@GetMapping("/available")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAvailable() {
		log.info("API CALL: Get available doctors");
		return ResponseEntity.ok(ApiResponse.success(doctorService.getAvailableDoctors(), "Available doctors fetched"));
	}

	@Operation(summary = "Get doctors by specialization", description = "Case-insensitive specialization filter e.g. Cardiology")
	@GetMapping("/specialization/{specialization}")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> getBySpecialization(
			@Parameter(description = "Specialization", example = "Cardiology") @PathVariable String specialization) {
		log.info("API CALL: Get doctors by specialization {}", specialization);
		return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorsBySpecialization(specialization),
				"Doctors fetched by specialization"));
	}

	@Operation(summary = "Create new doctor", description = "Registers a new doctor. License number must be unique.")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Doctor created"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed") })
	@PostMapping
	public ResponseEntity<ApiResponse<DoctorDTO>> create(@Valid @RequestBody DoctorDTO dto) {
		log.info("API CALL: Create doctor {} {}", dto.getFirstName(), dto.getLastName());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(doctorService.createDoctor(dto), "Doctor created successfully"));
	}

	@Operation(summary = "Update doctor details")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<DoctorDTO>> update(@Parameter(description = "Doctor ID") @PathVariable Long id,
			@Valid @RequestBody DoctorDTO dto) {
		log.info("API CALL: Update doctor id {}", id);
		return ResponseEntity
				.ok(ApiResponse.success(doctorService.updateDoctor(id, dto), "Doctor updated successfully"));
	}

	@Operation(summary = "Delete doctor")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@Parameter(description = "Doctor ID") @PathVariable Long id) {
		log.warn("API CALL: Delete doctor id {}", id);
		doctorService.deleteDoctor(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Doctor deleted successfully"));
	}

	@Operation(summary = "Search doctors by name", description = "Partial, case-insensitive search on first or last name")
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> searchByName(
			@Parameter(description = "Name keyword", example = "Sharma") @RequestParam String name) {
		log.info("API CALL: Search doctors name={}", name);
		return ResponseEntity
				.ok(ApiResponse.success(doctorService.searchDoctorsByName(name), "Doctors search completed"));
	}

	@Operation(summary = "Get total doctor count")
	@GetMapping("/count")
	public ResponseEntity<ApiResponse<Long>> count() {
		log.info("API CALL: Get doctor count");
		return ResponseEntity.ok(ApiResponse.success(doctorService.getTotalCount(), "Doctor count fetched"));
	}

	@Operation(summary = "Filter doctors by availability")
	@GetMapping("/filter")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> filterByAvailability(
			@Parameter(description = "true = available, false = unavailable", example = "true") @RequestParam boolean available) {
		log.info("API CALL: Filter doctors by availability={}", available);
		return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorsByAvailability(available),
				"Doctors filtered by availability"));
	}

	@Operation(summary = "Update doctor availability", description = "Toggle a doctor's availability for appointment booking")
	@PatchMapping("/{id}/availability")
	public ResponseEntity<ApiResponse<DoctorDTO>> updateAvailability(
			@Parameter(description = "Doctor ID") @PathVariable Long id,
			@Parameter(description = "true = available") @RequestParam boolean available) {
		log.info("API CALL: Update doctor availability id={} available={}", id, available);
		return ResponseEntity.ok(
				ApiResponse.success(doctorService.updateAvailability(id, available), "Doctor availability updated"));
	}
}