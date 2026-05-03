package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.DoctorDTO;
import com.hospital.backend.service.DoctorService;

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
public class DoctorController {

	private final DoctorService doctorService;

	// GET ALL
	@GetMapping
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAll() {
		log.info("API CALL: Get all doctors");
		return ResponseEntity.ok(ApiResponse.success(doctorService.getAllDoctors(), "Doctors fetched successfully"));
	}

	// GET BY ID
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<DoctorDTO>> getById(@PathVariable Long id) {
		log.info("API CALL: Get doctor by id {}", id);
		return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorById(id), "Doctor fetched successfully"));
	}

	// GET AVAILABLE
	@GetMapping("/available")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> getAvailable() {
		log.info("API CALL: Get available doctors");
		return ResponseEntity.ok(ApiResponse.success(doctorService.getAvailableDoctors(), "Available doctors fetched"));
	}

	// GET BY SPECIALIZATION
	@GetMapping("/specialization/{specialization}")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> getBySpecialization(@PathVariable String specialization) {

		log.info("API CALL: Get doctors by specialization {}", specialization);

		return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorsBySpecialization(specialization),
				"Doctors fetched by specialization"));
	}

	// CREATE
	@PostMapping
	public ResponseEntity<ApiResponse<DoctorDTO>> create(@Valid @RequestBody DoctorDTO dto) {

		log.info("API CALL: Create doctor {} {}", dto.getFirstName(), dto.getLastName());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(doctorService.createDoctor(dto), "Doctor created successfully"));
	}

	// UPDATE
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<DoctorDTO>> update(@PathVariable Long id, @Valid @RequestBody DoctorDTO dto) {

		log.info("API CALL: Update doctor id {}", id);

		return ResponseEntity
				.ok(ApiResponse.success(doctorService.updateDoctor(id, dto), "Doctor updated successfully"));
	}

	// DELETE
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.warn("API CALL: Delete doctor id {}", id);
		doctorService.deleteDoctor(id);

		return ResponseEntity.ok(ApiResponse.success(null, "Doctor deleted successfully"));
	}

	// SEARCH
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> searchByName(@RequestParam String name) {

		log.info("API CALL: Search doctors name={}", name);

		return ResponseEntity
				.ok(ApiResponse.success(doctorService.searchDoctorsByName(name), "Doctors search completed"));
	}

	// COUNT
	@GetMapping("/count")
	public ResponseEntity<ApiResponse<Long>> count() {

		log.info("API CALL: Get doctor count");

		return ResponseEntity.ok(ApiResponse.success(doctorService.getTotalCount(), "Doctor count fetched"));
	}

	// FILTER BY AVAILABILITY
	@GetMapping("/filter")
	public ResponseEntity<ApiResponse<List<DoctorDTO>>> filterByAvailability(@RequestParam boolean available) {

		log.info("API CALL: Filter doctors by availability={}", available);

		return ResponseEntity.ok(ApiResponse.success(doctorService.getDoctorsByAvailability(available),
				"Doctors filtered by availability"));
	}

	// UPDATE AVAILABILITY
	@PatchMapping("/{id}/availability")
	public ResponseEntity<ApiResponse<DoctorDTO>> updateAvailability(@PathVariable Long id,
			@RequestParam boolean available) {

		log.info("API CALL: Update doctor availability id={} available={}", id, available);

		return ResponseEntity.ok(
				ApiResponse.success(doctorService.updateAvailability(id, available), "Doctor availability updated"));
	}
}