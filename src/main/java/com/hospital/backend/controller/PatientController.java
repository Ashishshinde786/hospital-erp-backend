package com.hospital.backend.controller;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This class manages Patient APIs.

It handles:

Create patient

Get patient

Update patient

Delete patient

Search patients

Count patients

---------------------------------------------------------

This is called Patient Master Management.

In ERP:

Patient is central entity.

Other modules depend on it:

Appointment -> needs patient

Billing -> needs patient

Pharmacy -> may need patient

Medical records -> need patient
=========================================================
*/

import com.hospital.backend.dto.ApiResponse;

import com.hospital.backend.dto.PatientDTO;

import com.hospital.backend.service.PatientService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
Marks this as REST API controller.

Returns JSON.
*/
@RestController

/*
 * Base URL
 * 
 * All endpoints start with:
 * 
 * /api/patients
 */
@RequestMapping("/api/patients")

/*
 * Constructor injection.
 * 
 * Spring injects dependencies.
 */
@RequiredArgsConstructor
public class PatientController {

	/*
	 * Relationship:
	 * 
	 * PatientController
	 * 
	 * -> PatientService
	 * 
	 * -> PatientRepository
	 * 
	 * -> patients table
	 */
	private final PatientService patientService;

	/*
	 * GET
	 * 
	 * /api/patients
	 * 
	 * Fetch all patients.
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getAll() {
		return ResponseEntity.ok(ApiResponse.success(patientService.getAllPatients(), "Patients fetched successfully"));
	}

	/*
	 * GET
	 * 
	 * /api/patients/5
	 * 
	 * Fetch patient by ID.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> getById(@PathVariable Long id) {
		return ResponseEntity
				.ok(ApiResponse.success(patientService.getPatientById(id), "Patient fetched successfully"));
	}

	/*
	 * POST
	 * 
	 * Create patient.
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<PatientDTO>> create(
			/*
			 * Validate request body.
			 */
			@Valid @RequestBody PatientDTO dto

	) {
		/*
		 * Save patient.
		 */
		PatientDTO created = patientService.createPatient(dto);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(created, "Patient created successfully"));
	}

	/*
	 * PUT
	 * 
	 * Update patient.
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> update(@PathVariable Long id, @Valid @RequestBody PatientDTO dto) {
		return ResponseEntity
				.ok(ApiResponse.success(patientService.updatePatient(id, dto), "Patient updated successfully"));
	}

	/*
	 * DELETE patient.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		patientService.deletePatient(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Patient deleted successfully"));
	}

	/*
	 * Search patients.
	 * 
	 * Example:
	 * 
	 * /api/patients/search?q=John
	 */
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> search(
			/*
			 * Query parameter
			 * 
			 * q=John
			 */
			@RequestParam String q) {
		return ResponseEntity.ok(ApiResponse.success(patientService.searchPatients(q), "Search completed"));
	}

	/*
	 * Get total patient count.
	 * 
	 * Dashboard endpoint.
	 * 
	 * Used in ERP statistics.
	 */
	@GetMapping("/count")
	public ResponseEntity<ApiResponse<Long>> count() {
		return ResponseEntity.ok(ApiResponse.success(patientService.getTotalCount(), "Count fetched"));
	}

	/*
	 * GET
	 * 
	 * /api/patients/blood-group?group=O+
	 * 
	 * Fetch patients by blood group.
	 * 
	 * Useful for:
	 * 
	 * Emergency
	 * 
	 * Blood matching
	 * 
	 * Medical filtering
	 */
	@GetMapping("/blood-group")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getByBloodGroup(@RequestParam String group) {
		return ResponseEntity.ok(
				ApiResponse.success(patientService.getPatientsByBloodGroup(group), "Patients fetched by blood group"));
	}

}