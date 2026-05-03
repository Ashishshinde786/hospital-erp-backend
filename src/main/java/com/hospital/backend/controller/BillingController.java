package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.InvoiceDTO;
import com.hospital.backend.entity.Invoice.PaymentMethod;
import com.hospital.backend.service.BillingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

	private final BillingService billingService;

	// GET ALL
	@GetMapping
	public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAll() {
		log.info("API CALL: Get all invoices");
		return ResponseEntity.ok(ApiResponse.success(billingService.getAllInvoices(), "Invoices fetched"));
	}

	// GET BY ID
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<InvoiceDTO>> getById(@PathVariable Long id) {
		log.info("API CALL: Get invoice by id {}", id);
		return ResponseEntity.ok(ApiResponse.success(billingService.getInvoiceById(id), "Invoice fetched"));
	}

	// GET BY PATIENT
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getByPatient(@PathVariable Long patientId) {
		log.info("API CALL: Get invoices for patient {}", patientId);
		return ResponseEntity.ok(ApiResponse.success(billingService.getInvoicesByPatient(patientId), "Fetched"));
	}

	// CREATE
	@PostMapping
	public ResponseEntity<ApiResponse<InvoiceDTO>> create(@Valid @RequestBody InvoiceDTO dto) {
		log.info("API CALL: Create invoice for patient {}", dto.getPatientId());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(billingService.createInvoice(dto), "Invoice created"));
	}

	// PROCESS PAYMENT
	@PostMapping("/{id}/payment")
	public ResponseEntity<ApiResponse<InvoiceDTO>> processPayment(@PathVariable Long id,
			@RequestParam BigDecimal amount, @RequestParam PaymentMethod method) {

		log.info("API CALL: Process payment for invoice {} amount={} method={}", id, amount, method);

		return ResponseEntity
				.ok(ApiResponse.success(billingService.processPayment(id, amount, method), "Payment processed"));
	}

	// TOTAL REVENUE
	@GetMapping("/revenue")
	public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenue() {
		log.info("API CALL: Get total revenue");
		return ResponseEntity.ok(ApiResponse.success(billingService.getTotalRevenue(), "Revenue fetched"));
	}
}