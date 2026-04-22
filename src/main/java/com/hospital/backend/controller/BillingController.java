package com.hospital.backend.controller;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This is Billing REST Controller.

It exposes APIs for:

Create invoice

Get invoice

Get patient invoices

Process payment

Calculate revenue

---------------------------------------------------------

This handles financial operations.

In hospital ERP:

Patient consults doctor

↓

Appointment created

↓

Invoice generated

↓

Payment collected

↓

Revenue tracked

This controller exposes APIs
for those operations.
=========================================================
*/

import com.hospital.backend.dto.ApiResponse;

import com.hospital.backend.dto.InvoiceDTO;

import com.hospital.backend.entity.Invoice.PaymentMethod;

import com.hospital.backend.service.BillingService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import java.util.List;

/*
Marks this as REST controller.

Returns JSON.
*/
@RestController

/*
 * Base URL:
 * 
 * /api/billing
 */
@RequestMapping("/api/billing")

/*
 * Generates constructor
 * 
 * injects BillingService
 */
@RequiredArgsConstructor
public class BillingController {

	/*
	 * Relationship:
	 * 
	 * BillingController
	 * 
	 * -> BillingService
	 * 
	 * -> InvoiceRepository
	 * 
	 * -> Invoice Entity
	 * 
	 * -> InvoiceItem
	 */
	private final BillingService billingService;

	/*
	 * GET
	 * 
	 * /api/billing
	 * 
	 * Fetch all invoices.
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getAll() {
		return ResponseEntity.ok(ApiResponse.success(billingService.getAllInvoices(), "Invoices fetched"));
	}

	/*
	 * GET
	 * 
	 * /api/billing/5
	 * 
	 * Fetch invoice by id.
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<InvoiceDTO>> getById(
			/*
			 * Read invoice id from URL.
			 */
			@PathVariable Long id) {
		return ResponseEntity.ok(ApiResponse.success(billingService.getInvoiceById(id), "Invoice fetched"));
	}

	/*
	 * GET
	 * 
	 * /api/billing/patient/1
	 * 
	 * Get all invoices for patient.
	 */
	@GetMapping("/patient/{patientId}")
	public ResponseEntity<ApiResponse<List<InvoiceDTO>>> getByPatient(@PathVariable Long patientId) {
		return ResponseEntity.ok(ApiResponse.success(billingService.getInvoicesByPatient(patientId), "Fetched"));
	}

	/*
	 * POST
	 * 
	 * Create invoice.
	 * 
	 * Example:
	 * 
	 * consultation charge
	 * 
	 * medicine charge
	 * 
	 * lab charges
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<InvoiceDTO>> create(
			/*
			 * Read JSON request body.
			 */
			@RequestBody
			/*
			 * Validate DTO.
			 */
			@Valid InvoiceDTO dto) {
		return ResponseEntity
				/*
				 * 201 Created
				 */
				.status(HttpStatus.CREATED)
				.body(ApiResponse.success(billingService.createInvoice(dto), "Invoice created"));
	}

	/*
	 * PROCESS PAYMENT API
	 * 
	 * This is business operation.
	 * 
	 * not normal CRUD.
	 * 
	 * URL:
	 * 
	 * /api/billing/1/payment
	 */
	@PostMapping("/{id}/payment")
	public ResponseEntity<ApiResponse<InvoiceDTO>> processPayment(
			/*
			 * invoice id
			 */
			@PathVariable Long id,
			/*
			 * Payment amount
			 * 
			 * Example:
			 * 
			 * 500
			 */
			@RequestParam BigDecimal amount,
			/*
			 * Payment method:
			 * 
			 * CASH
			 * 
			 * CARD
			 * 
			 * UPI
			 * 
			 * INSURANCE
			 */
			@RequestParam PaymentMethod method) {
		return ResponseEntity.ok(ApiResponse.success(billingService.processPayment(id, amount, method),
				"Payment processed successfully"));
	}

	/*
	 * GET total revenue.
	 * 
	 * Business analytics endpoint.
	 */
	@GetMapping("/revenue")
	public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenue() {
		return ResponseEntity.ok(ApiResponse.success(billingService.getTotalRevenue(), "Revenue fetched"));
	}

}