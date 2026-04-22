package com.hospital.backend.controller;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This is Doctor REST Controller.

It exposes APIs for:

Create doctor

Get doctor

Update doctor

Delete doctor

Find available doctors

Find doctors by specialization

---------------------------------------------------------

In hospital ERP:

Doctors are core master data.

Appointments depend on doctors.

Scheduling depends on doctors.

Billing may depend on doctor consultation.

This controller manages those APIs.
=========================================================
*/

import com.hospital.backend.dto.ApiResponse;

import com.hospital.backend.dto.DoctorDTO;

import com.hospital.backend.service.DoctorService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
REST API controller.

Returns JSON.
*/
@RestController

/*
 * Base URL
 * 
 * /api/doctors
 */
@RequestMapping("/api/doctors")

/*
 * Constructor injection
 */
@RequiredArgsConstructor
public class DoctorController {

	/*
	 * Relationship:
	 * 
	 * DoctorController
	 * 
	 * -> DoctorService
	 * 
	 * -> DoctorRepository
	 * 
	 * -> doctors table
	 */
	private final DoctorService doctorService;

	/*
	 * GET
	 * 
	 * /api/doctors
	 * 
	 * Fetch all doctors.
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAll() {
		return ResponseEntity.ok(ApiResponse.success(doctorService.getAllDoctors(), "Doctors fetched successfully"));
	}

	/*
	 * GET
	 * 
	 * /api/doctors/5
	 * 
	 * Fetch doctor by id.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<DoctorDTO>> getById(
			/*
			 * Read doctor id from URL.
			 */
			@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorById(id), "Doctor fetched successfully"));
	}

	/*
	 * GET
	 * 
	 * /api/doctors/available
	 * 
	 * Find only available doctors.
	 */
	@GetMapping("/available")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAvailable() {
		return ResponseEntity.ok(ApiResponse.success(doctorService.getAvailableDoctors(), "Available doctors fetched"));
	}

	/*
	 * GET
	 * 
	 * /api/doctors/specialization/Cardiology
	 * 
	 * Find doctors by specialization.
	 */
	@GetMapping("/specialization/{specialization}")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> getBySpecialization(
			/*
			 * Read specialization from URL.
			 * 
			 * Example:
			 * 
			 * Cardiology
			 */
			@PathVariable String specialization) {
		return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorsBySpecialization(specialization),
				"Doctors fetched by specialization"

		));
	}

	/*
	 * POST
	 * 
	 * Create new doctor.
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<DoctorDTO>> create(
			/*
			 * Read JSON body.
			 */
			@RequestBody
			/*
			 * Validate DTO.
			 */
			@Valid DoctorDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(doctorService.createDoctor(dto), "Doctor created successfully"));
	}

	/*
	 * PUT
	 * 
	 * Full doctor update.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<DoctorDTO>> update(@PathVariable Long id, @RequestBody @Valid DoctorDTO dto) {
		return ResponseEntity
				.ok(ApiResponse.success(doctorService.updateDoctor(id, dto), "Doctor updated successfully"));
	}

	/*
	 * DELETE doctor.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		doctorService.deleteDoctor(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Doctor deleted successfully"));
	}

}