package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.PatientDTO;
import com.hospital.backend.service.PatientService;

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
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Patient master data management — CRUD, search, blood group, age range")
public class PatientController {

	private final PatientService patientService;

	@Operation(summary = "Get all patients", description = "Returns list of all registered patients")
	@GetMapping
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getAll() {
		log.info("API CALL: Get all patients");
		return ResponseEntity.ok(ApiResponse.success(patientService.getAllPatients(), "Patients fetched successfully"));
	}

	@Operation(summary = "Get patient by ID")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Patient found"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Patient not found") })
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> getById(
			@Parameter(description = "Patient ID", example = "1") @PathVariable Long id) {
		log.info("API CALL: Get patient by id {}", id);
		return ResponseEntity
				.ok(ApiResponse.success(patientService.getPatientById(id), "Patient fetched successfully"));
	}

	@Operation(summary = "Create new patient", description = "Registers a new patient into the system")
	@ApiResponses(value = {
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Patient created"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation failed") })
	@PostMapping
	public ResponseEntity<ApiResponse<PatientDTO>> create(@Valid @RequestBody PatientDTO dto) {
		log.info("API CALL: Create patient {} {}", dto.getFirstName(), dto.getLastName());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(patientService.createPatient(dto), "Patient created successfully"));
	}

	@Operation(summary = "Update patient", description = "Updates all fields of an existing patient")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> update(@Parameter(description = "Patient ID") @PathVariable Long id,
			@Valid @RequestBody PatientDTO dto) {
		log.info("API CALL: Update patient id {}", id);
		return ResponseEntity
				.ok(ApiResponse.success(patientService.updatePatient(id, dto), "Patient updated successfully"));
	}

	@Operation(summary = "Delete patient", description = "Permanently deletes a patient record")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@Parameter(description = "Patient ID") @PathVariable Long id) {
		log.warn("API CALL: Delete patient id {}", id);
		patientService.deletePatient(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Patient deleted successfully"));
	}

	@Operation(summary = "Search patients", description = "Search patients by first or last name (case-insensitive partial match)")
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> search(
			@Parameter(description = "Search query", example = "Ashish") @RequestParam String q) {
		log.info("API CALL: Search patients query {}", q);
		return ResponseEntity.ok(ApiResponse.success(patientService.searchPatients(q), "Search completed"));
	}

	@Operation(summary = "Get total patient count")
	@GetMapping("/count")
	public ResponseEntity<ApiResponse<Long>> count() {
		log.info("API CALL: Count patients");
		return ResponseEntity.ok(ApiResponse.success(patientService.getTotalCount(), "Count fetched"));
	}

	@Operation(summary = "Get patients by blood group", description = "Filter patients by blood group e.g. A+, B-, O+, AB+")
	@GetMapping("/blood-group")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getByBloodGroup(
			@Parameter(description = "Blood group", example = "O+") @RequestParam String group) {
		log.info("API CALL: Get patients by blood group {}", group);
		return ResponseEntity.ok(
				ApiResponse.success(patientService.getPatientsByBloodGroup(group), "Patients fetched by blood group"));
	}

	@Operation(summary = "Get patients by age range", description = "Returns patients whose age falls between min and max")
	@GetMapping("/age-range")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getByAgeRange(
			@Parameter(description = "Minimum age", example = "20") @RequestParam int min,
			@Parameter(description = "Maximum age", example = "60") @RequestParam int max) {
		log.info("API CALL: Get patients age {} to {}", min, max);
		return ResponseEntity.ok(
				ApiResponse.success(patientService.getPatientsByAgeRange(min, max), "Patients fetched by age range"));
	}

	@Operation(summary = "Get recently registered patients", description = "Returns patients registered in the last N days (default 7)")
	@GetMapping("/recent")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getRecentPatients(
			@Parameter(description = "Number of days", example = "7") @RequestParam(defaultValue = "7") int days) {
		log.info("API CALL: Get recent patients last {} days", days);
		return ResponseEntity
				.ok(ApiResponse.success(patientService.getRecentPatients(days), "Recent patients fetched"));
	}
}