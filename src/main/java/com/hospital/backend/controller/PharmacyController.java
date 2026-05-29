package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.MedicineDTO;
import com.hospital.backend.service.PharmacyService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/api/pharmacy")
@RequiredArgsConstructor
@Tag(name = "Pharmacy", description = "Medicine inventory — stock management, expiry tracking, low stock alerts")
public class PharmacyController {

	private final PharmacyService pharmacyService;

	@Operation(summary = "Get all active medicines", description = "Returns only medicines where active=true (soft-delete aware)")
	@GetMapping
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> getAll() {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.getAllMedicines(), "Medicines fetched"));
	}

	@Operation(summary = "Get medicine by ID")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<MedicineDTO>> getById(
			@Parameter(description = "Medicine ID", example = "1") @PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.getMedicineById(id), "Medicine fetched"));
	}

	@Operation(summary = "Search medicines by name", description = "Partial, case-insensitive name search e.g. 'para' finds Paracetamol")
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> search(
			@Parameter(description = "Medicine name keyword", example = "Paracetamol") @RequestParam String name) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.searchMedicines(name), "Search completed"));
	}

	@Operation(summary = "Get low stock medicines", description = "Returns medicines where stock quantity is below the threshold")
	@GetMapping("/low-stock")
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> getLowStock(
			@Parameter(description = "Stock threshold (default 10)", example = "10") @RequestParam(defaultValue = "10") int threshold) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.getLowStockMedicines(threshold), "Fetched"));
	}

	@Operation(summary = "Add new medicine", description = "Creates a new medicine record in inventory")
	@PostMapping
	public ResponseEntity<ApiResponse<MedicineDTO>> create(@Valid @RequestBody MedicineDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(pharmacyService.createMedicine(dto), "Medicine created"));
	}

	@Operation(summary = "Update medicine details", description = "Update price, manufacturer, expiry date, batch number etc.")
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<MedicineDTO>> update(
			@Parameter(description = "Medicine ID") @PathVariable Long id, @Valid @RequestBody MedicineDTO dto) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.updateMedicine(id, dto), "Medicine updated"));
	}

	@Operation(summary = "Update stock quantity", description = "Adds the given quantity to current stock. Use negative value to reduce.")
	@PatchMapping("/{id}/stock")
	public ResponseEntity<ApiResponse<MedicineDTO>> updateStock(
			@Parameter(description = "Medicine ID") @PathVariable Long id,
			@Parameter(description = "Quantity to add", example = "50") @RequestParam int quantity) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.updateStock(id, quantity), "Stock updated"));
	}

	@Operation(summary = "Deactivate medicine", description = "Soft delete — sets active=false. Record is NOT removed from DB.")
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@Parameter(description = "Medicine ID") @PathVariable Long id) {
		pharmacyService.deleteMedicine(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Medicine deactivated"));
	}

	@Operation(summary = "Get expired medicines", description = "Returns active medicines whose expiry date is before today")
	@GetMapping("/expired")
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> getExpiredMedicines() {
		return ResponseEntity
				.ok(ApiResponse.success(pharmacyService.getExpiredMedicines(), "Expired medicines fetched"));
	}
}