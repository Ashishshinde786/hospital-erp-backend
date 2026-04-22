package com.hospital.backend.service;

import com.hospital.backend.dto.InvoiceDTO;

import com.hospital.backend.entity.*;

import com.hospital.backend.entity.Invoice.PaymentStatus;

import com.hospital.backend.exception.ResourceNotFoundException;

import com.hospital.backend.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.List;

import java.util.stream.Collectors;

/*
=======================================================

BILLING SERVICE

Business Logic Layer for Billing Module

Handles:

1 Create invoice

2 Create invoice items

3 Calculate totals

4 Process payments

5 Update payment status

6 Revenue calculations

=======================================================
*/

@Service
@RequiredArgsConstructor

/*
 * Everything runs inside DB transaction.
 * 
 * If one insert fails:
 * 
 * rollback everything.
 * 
 * Important for financial consistency.
 */
@Transactional
public class BillingService {

	/*
	 * =============================================== DEPENDENCIES
	 * ===============================================
	 */

	/*
	 * invoices table operations
	 */
	private final InvoiceRepository invoiceRepository;

	/*
	 * used to fetch patient
	 */
	private final PatientRepository patientRepository;

	/*
	 * optional appointment link
	 */
	private final AppointmentRepository appointmentRepository;

	/*
	 * =============================================== GET ALL INVOICES
	 * ===============================================
	 */
	@Transactional(readOnly = true)
	public List<InvoiceDTO> getAllInvoices() {

		return invoiceRepository

				.findAll()

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * =============================================== GET SINGLE INVOICE
	 * ===============================================
	 */
	@Transactional(readOnly = true)
	public InvoiceDTO getInvoiceById(Long id) {

		return toDTO(

				findById(id)

		);
	}

	/*
	 * =============================================== GET PATIENT INVOICES
	 * ===============================================
	 */
	@Transactional(readOnly = true)
	public List<InvoiceDTO> getInvoicesByPatient(Long patientId) {

		return invoiceRepository

				.findByPatientId(patientId)

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * =============================================== CREATE INVOICE
	 * ===============================================
	 * 
	 * MOST IMPORTANT METHOD
	 * 
	 * Steps:
	 * 
	 * 1 find patient
	 * 
	 * 2 create invoice items
	 * 
	 * 3 calculate total
	 * 
	 * 4 build invoice
	 * 
	 * 5 link appointment
	 * 
	 * 6 set back references
	 * 
	 * 7 save invoice
	 */
	public InvoiceDTO createInvoice(InvoiceDTO dto) {

		/*
		 * STEP 1
		 * 
		 * Validate patient exists
		 */
		Patient patient =

				patientRepository.findById(dto.getPatientId())

						.orElseThrow(

								() -> new ResourceNotFoundException("Patient not found"));

		/*
		 * STEP 2
		 * 
		 * Convert DTO items -> Entity items
		 * 
		 * JSON:
		 * 
		 * Consultation 500 x 2
		 * 
		 * becomes InvoiceItem entity
		 */
		List<InvoiceItem> items =

				dto.getItems()

						.stream()

						.map(itemDTO -> {

							/*
							 * calculate line total
							 * 
							 * qty * price
							 */
							BigDecimal total =

									itemDTO.getUnitPrice()

											.multiply(

													BigDecimal.valueOf(itemDTO.getQuantity()));

							return InvoiceItem.builder()

									.description(itemDTO.getDescription())

									.quantity(itemDTO.getQuantity())

									.unitPrice(itemDTO.getUnitPrice())

									.totalPrice(total)

									.itemType(itemDTO.getItemType())

									.build();

						})

						.collect(Collectors.toList());

		/*
		 * STEP 3
		 * 
		 * Calculate full invoice total
		 * 
		 * add all item totals
		 */
		BigDecimal total =

				items.stream()

						.map(InvoiceItem::getTotalPrice)

						.reduce(

								BigDecimal.ZERO,

								BigDecimal::add);

		/*
		 * STEP 4
		 * 
		 * Build invoice
		 */
		Invoice invoice =

				Invoice.builder()

						.patient(patient)

						.items(items)

						.totalAmount(total)

						.paidAmount(BigDecimal.ZERO)

						.paymentStatus(PaymentStatus.PENDING)

						.build();

		/*
		 * STEP 5
		 * 
		 * Optional appointment link
		 */
		if (dto.getAppointmentId() != null) {

			appointmentRepository

					.findById(dto.getAppointmentId())

					.ifPresent(

							invoice::setAppointment);
		}

		/*
		 * STEP 6
		 * 
		 * VERY IMPORTANT
		 * 
		 * Fixes your earlier bug:
		 * 
		 * invoice_id cannot be null
		 * 
		 * Every child must point to parent
		 */
		items.forEach(

				item -> item.setInvoice(invoice)

		);

		/*
		 * STEP 7
		 * 
		 * save parent
		 * 
		 * Cascade saves children automatically
		 */
		Invoice saved =

				invoiceRepository.save(invoice);

		return toDTO(saved);

	}

	/*
	 * =============================================== PROCESS PAYMENT
	 * ===============================================
	 */
	public InvoiceDTO processPayment(

			Long invoiceId,

			BigDecimal amount,

			Invoice.PaymentMethod method

	) {

		Invoice invoice =

				findById(invoiceId);

		/*
		 * add payment
		 */
		BigDecimal newPaid =

				invoice.getPaidAmount()

						.add(amount);

		invoice.setPaidAmount(newPaid);

		invoice.setPaymentMethod(method);

		/*
		 * Fully paid?
		 */
		if (

		newPaid.compareTo(

				invoice.getTotalAmount()

		) >= 0

		) {

			invoice.setPaymentStatus(

					PaymentStatus.PAID

			);

			invoice.setPaidDate(

					LocalDateTime.now()

			);
		}

		else {

			invoice.setPaymentStatus(

					PaymentStatus.PARTIAL

			);
		}

		return toDTO(

				invoiceRepository.save(invoice)

		);

	}

	/*
	 * =============================================== TOTAL REVENUE
	 * ===============================================
	 */
	@Transactional(readOnly = true)
	public BigDecimal getTotalRevenue() {

		BigDecimal revenue =

				invoiceRepository.getTotalRevenue();

		return revenue != null

				? revenue

				: BigDecimal.ZERO;
	}

	/*
	 * =============================================== HELPER
	 * 
	 * Find invoice ===============================================
	 */
	private Invoice findById(Long id) {

		return invoiceRepository

				.findById(id)

				.orElseThrow(

						() -> new ResourceNotFoundException(

								"Invoice not found with id:" + id

						)

				);
	}

	/*
	 * =============================================== ENTITY -> DTO conversion
	 * ===============================================
	 */
	private InvoiceDTO toDTO(Invoice inv) {

		List<InvoiceDTO.InvoiceItemDTO> itemDTOs =

				inv.getItems() == null

						? List.of()

						:

						inv.getItems()

								.stream()

								.map(item ->

								InvoiceDTO.InvoiceItemDTO

										.builder()

										.description(item.getDescription())

										.quantity(item.getQuantity())

										.unitPrice(item.getUnitPrice())

										.itemType(item.getItemType())

										.build()

								)

								.collect(Collectors.toList());

		return InvoiceDTO.builder()

				.id(inv.getId())

				.patientId(inv.getPatient().getId())

				.patientName(

						inv.getPatient().getFirstName()

								+ " " +

								inv.getPatient().getLastName()

				)

				.appointmentId(

						inv.getAppointment() != null

								?

								inv.getAppointment().getId()

								:

								null)

				.items(itemDTOs)

				.totalAmount(inv.getTotalAmount())

				.paidAmount(inv.getPaidAmount())

				.paymentStatus(inv.getPaymentStatus())

				.paymentMethod(inv.getPaymentMethod())

				.build();

	}

}