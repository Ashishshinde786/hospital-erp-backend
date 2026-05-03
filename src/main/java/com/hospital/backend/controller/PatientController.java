package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.PatientDTO;
import com.hospital.backend.service.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

	private final PatientService patientService;

	// GET ALL
	@GetMapping
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getAll() {
		log.info("API CALL: Get all patients");
		return ResponseEntity.ok(ApiResponse.success(patientService.getAllPatients(), "Patients fetched successfully"));
	}

	// GET BY ID
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> getById(@PathVariable Long id) {
		log.info("API CALL: Get patient by id {}", id);
		return ResponseEntity
				.ok(ApiResponse.success(patientService.getPatientById(id), "Patient fetched successfully"));
	}

	// CREATE
	@PostMapping
	public ResponseEntity<ApiResponse<PatientDTO>> create(@Valid @RequestBody PatientDTO dto) {
		log.info("API CALL: Create patient {} {}", dto.getFirstName(), dto.getLastName());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(patientService.createPatient(dto), "Patient created successfully"));
	}

	// UPDATE
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<PatientDTO>> update(@PathVariable Long id, @Valid @RequestBody PatientDTO dto) {

		log.info("API CALL: Update patient id {}", id);
		return ResponseEntity
				.ok(ApiResponse.success(patientService.updatePatient(id, dto), "Patient updated successfully"));
	}

	// DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.warn("API CALL: Delete patient id {}", id);
		patientService.deletePatient(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Patient deleted successfully"));
	}

	// SEARCH
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> search(@RequestParam String q) {
		log.info("API CALL: Search patients query {}", q);
		return ResponseEntity.ok(ApiResponse.success(patientService.searchPatients(q), "Search completed"));
	}

	// COUNT
	@GetMapping("/count")
	public ResponseEntity<ApiResponse<Long>> count() {
		log.info("API CALL: Count patients");
		return ResponseEntity.ok(ApiResponse.success(patientService.getTotalCount(), "Count fetched"));
	}

	// BLOOD GROUP
	@GetMapping("/blood-group")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getByBloodGroup(@RequestParam String group) {
		log.info("API CALL: Get patients by blood group {}", group);
		return ResponseEntity.ok(
				ApiResponse.success(patientService.getPatientsByBloodGroup(group), "Patients fetched by blood group"));
	}

	// AGE RANGE
	@GetMapping("/age-range")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getByAgeRange(@RequestParam int min, @RequestParam int max) {

		log.info("API CALL: Get patients age {} to {}", min, max);

		return ResponseEntity.ok(
				ApiResponse.success(patientService.getPatientsByAgeRange(min, max), "Patients fetched by age range"));
	}

	// RECENT
	@GetMapping("/recent")
	public ResponseEntity<ApiResponse<List<PatientDTO>>> getRecentPatients(@RequestParam(defaultValue = "7") int days) {

		log.info("API CALL: Get recent patients last {} days", days);

		return ResponseEntity
				.ok(ApiResponse.success(patientService.getRecentPatients(days), "Recent patients fetched"));
	}
}