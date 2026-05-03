package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.MedicineDTO;
import com.hospital.backend.service.PharmacyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
public class PharmacyController {

	private final PharmacyService pharmacyService;

	// GET ALL
	@GetMapping
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> getAll() {
		log.info("API CALL: Get all medicines");
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.getAllMedicines(), "Medicines fetched"));
	}

	// GET BY ID
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<MedicineDTO>> getById(@PathVariable Long id) {
		log.info("API CALL: Get medicine by id {}", id);
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.getMedicineById(id), "Medicine fetched"));
	}

	// SEARCH
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> search(@RequestParam String name) {
		log.info("API CALL: Search medicines name={}", name);
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.searchMedicines(name), "Search completed"));
	}

	// LOW STOCK
	@GetMapping("/low-stock")
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> getLowStock(
			@RequestParam(defaultValue = "10") int threshold) {

		log.info("API CALL: Get low stock medicines threshold={}", threshold);

		return ResponseEntity.ok(ApiResponse.success(pharmacyService.getLowStockMedicines(threshold), "Fetched"));
	}

	// CREATE
	@PostMapping
	public ResponseEntity<ApiResponse<MedicineDTO>> create(@Valid @RequestBody MedicineDTO dto) {
		log.info("API CALL: Create medicine {}", dto.getName());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(pharmacyService.createMedicine(dto), "Medicine created"));
	}

	// UPDATE
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<MedicineDTO>> update(@PathVariable Long id, @Valid @RequestBody MedicineDTO dto) {

		log.info("API CALL: Update medicine id {}", id);

		return ResponseEntity.ok(ApiResponse.success(pharmacyService.updateMedicine(id, dto), "Medicine updated"));
	}

	// UPDATE STOCK
	@PatchMapping("/{id}/stock")
	public ResponseEntity<ApiResponse<MedicineDTO>> updateStock(@PathVariable Long id, @RequestParam int quantity) {

		log.info("API CALL: Update stock id {} quantity {}", id, quantity);

		return ResponseEntity.ok(ApiResponse.success(pharmacyService.updateStock(id, quantity), "Stock updated"));
	}

	// DELETE (SOFT)
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		log.warn("API CALL: Deactivate medicine id {}", id);
		pharmacyService.deleteMedicine(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Medicine deactivated"));
	}

	// EXPIRED
	@GetMapping("/expired")
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> getExpiredMedicines() {
		log.info("API CALL: Get expired medicines");
		return ResponseEntity
				.ok(ApiResponse.success(pharmacyService.getExpiredMedicines(), "Expired medicines fetched"));
	}
}