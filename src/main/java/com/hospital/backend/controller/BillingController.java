package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.InvoiceDTO;
import com.hospital.backend.entity.Invoice.PaymentMethod;
import com.hospital.backend.service.BillingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Billing", description = "Invoice creation, payment processing, revenue reports")
public class BillingController {

	private final BillingService billingService;

	@Operation(summary = "Get all invoices")
	@GetMapping
	public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAll() {
		log.info("API CALL: Get all invoices");
		return ResponseEntity.ok(ApiResponse.success(billingService.getAllInvoices(), "Invoices fetched"));
	}

	@Operation(summary = "Get invoice by ID")
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<InvoiceDTO>> getById(
			@Parameter(description = "Invoice ID", example = "1") @PathVariable Long id) {
		log.info("API CALL: Get invoice by id {}", id);
		return ResponseEntity.ok(ApiResponse.success(billingService.getInvoiceById(id), "Invoice fetched"));
	}

	@Operation(summary = "Get invoices by patient")
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getByPatient(
			@Parameter(description = "Patient ID", example = "1") @PathVariable Long patientId) {
		log.info("API CALL: Get invoices for patient {}", patientId);
		return ResponseEntity.ok(ApiResponse.success(billingService.getInvoicesByPatient(patientId), "Fetched"));
	}

	@Operation(summary = "Create invoice", description = "Creates a new invoice with line items. Total is auto-calculated from items.")
	@PostMapping
	public ResponseEntity<ApiResponse<InvoiceDTO>> create(@Valid @RequestBody InvoiceDTO dto) {
		log.info("API CALL: Create invoice for patient {}", dto.getPatientId());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(billingService.createInvoice(dto), "Invoice created"));
	}

	@Operation(summary = "Process payment", description = "Record a payment against an invoice. Status auto-updates: PARTIAL if underpaid, PAID if fully paid.")
	@PostMapping("/{id}/payment")
	public ResponseEntity<ApiResponse<InvoiceDTO>> processPayment(
			@Parameter(description = "Invoice ID", example = "1") @PathVariable Long id,
			@Parameter(description = "Payment amount", example = "500.00") @RequestParam BigDecimal amount,
			@Parameter(description = "Payment method", example = "UPI") @RequestParam PaymentMethod method) {
		log.info("API CALL: Process payment for invoice {} amount={} method={}", id, amount, method);
		return ResponseEntity
				.ok(ApiResponse.success(billingService.processPayment(id, amount, method), "Payment processed"));
	}

	@Operation(summary = "Get total revenue", description = "Sum of totalAmount for all PAID invoices")
	@GetMapping("/revenue")
	public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenue() {
		log.info("API CALL: Get total revenue");
		return ResponseEntity.ok(ApiResponse.success(billingService.getTotalRevenue(), "Revenue fetched"));
	}

	@Operation(summary = "Get invoices by payment status", description = "Filter by: PENDING, PARTIAL, PAID, CANCELLED")
	@GetMapping("/status")
	public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getByStatus(
			@Parameter(description = "Payment status", example = "PENDING") @RequestParam com.hospital.backend.entity.Invoice.PaymentStatus status) {
		return ResponseEntity
				.ok(ApiResponse.success(billingService.getInvoicesByStatus(status), "Invoices fetched by status"));
	}
}