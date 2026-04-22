package com.hospital.backend.controller;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This class handles Pharmacy APIs.

It manages medicine inventory.

This is basically:

Inventory Management Controller

or

Pharmacy Module Controller


It handles:

Add medicine

Get medicines

Search medicines

Update medicine

Update stock

Check low stock

Deactivate medicine

---------------------------------------------------------

ERP Modules connected to this:

Billing

Prescription

Purchasing

Inventory

Doctor consultation

=========================================================
*/

import com.hospital.backend.dto.ApiResponse;

import com.hospital.backend.dto.MedicineDTO;

import com.hospital.backend.service.PharmacyService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
Marks as REST controller.

Returns JSON.
*/
@RestController

/*
 * Base route:
 * 
 * /api/pharmacy
 */
@RequestMapping("/api/pharmacy")

/*
 * Constructor injection
 */
@RequiredArgsConstructor
public class PharmacyController {

	/*
	 * Relationship:
	 * 
	 * PharmacyController
	 * 
	 * -> PharmacyService
	 * 
	 * -> MedicineRepository
	 * 
	 * -> medicines table
	 */
	private final PharmacyService pharmacyService;

	/*
	 * GET
	 * 
	 * Fetch all active medicines.
	 * 
	 * GET /api/pharmacy
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> getAll() {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.getAllMedicines(), "Medicines fetched"));
	}

	/*
	 * GET
	 * 
	 * Fetch medicine by ID
	 * 
	 * /api/pharmacy/5
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<MedicineDTO>> getById(@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.getMedicineById(id), "Medicine fetched"));
	}

	/*
	 * SEARCH medicine.
	 * 
	 * Example:
	 * 
	 * /api/pharmacy/search?name=Paracetamol
	 * 
	 * Used by:
	 * 
	 * Pharmacist
	 * 
	 * Doctor
	 * 
	 * Billing
	 */
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> search(
			/*
			 * Query parameter
			 * 
			 * name=Paracetamol
			 */
			@RequestParam String name) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.searchMedicines(name), "Search completed"));
	}

	/*
	 * Low stock alert endpoint.
	 * 
	 * ERP inventory feature.
	 * 
	 * Example:
	 * 
	 * Find medicines with stock < 10
	 * 
	 * /api/pharmacy/low-stock
	 */
	@GetMapping("/low-stock")
	public ResponseEntity<ApiResponse<List<MedicineDTO>>> getLowStock(
			/*
			 * If user does not pass threshold, default = 10
			 */
			@RequestParam(defaultValue = "10") int threshold) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.getLowStockMedicines(threshold), "Fetched"));
	}

	/*
	 * POST
	 * 
	 * Add new medicine.
	 * 
	 * Example:
	 * 
	 * Add Paracetamol.
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<MedicineDTO>> create(@Valid @RequestBody MedicineDTO dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(pharmacyService.createMedicine(dto), "Medicine created"));
	}

	/*
	 * Update medicine details.
	 * 
	 * Example:
	 * 
	 * Change price
	 * 
	 * Change manufacturer
	 * 
	 * Change expiry
	 */
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<MedicineDTO>> update(@PathVariable Long id, @Valid @RequestBody MedicineDTO dto) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.updateMedicine(id, dto), "Medicine updated"));
	}

	/*
	 * Update stock quantity.
	 * 
	 * Very important inventory API.
	 * 
	 * Example:
	 * 
	 * Received 50 tablets.
	 * 
	 * Increase stock.
	 */
	@PatchMapping("/{id}/stock")
	public ResponseEntity<ApiResponse<MedicineDTO>> updateStock(@PathVariable Long id, @RequestParam int quantity) {
		return ResponseEntity.ok(ApiResponse.success(pharmacyService.updateStock(id, quantity), "Stock updated"));
	}

	/*
	 * Delete medicine.
	 * 
	 * But actually:
	 * 
	 * Soft delete.
	 * 
	 * It deactivates medicine.
	 * 
	 * Does NOT remove row.
	 * 
	 * Very important ERP concept.
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
		pharmacyService.deleteMedicine(id);
		return ResponseEntity.ok(ApiResponse.success(null, "Medicine deactivated"));
	}

}