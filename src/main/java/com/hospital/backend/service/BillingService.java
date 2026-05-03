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

@Service
@RequiredArgsConstructor
@Transactional
public class BillingService {

	private final InvoiceRepository invoiceRepository;
	private final PatientRepository patientRepository;
	private final AppointmentRepository appointmentRepository;

	// ✅ GET ALL
	@Transactional(readOnly = true)
	public List<InvoiceDTO> getAllInvoices() {
		return invoiceRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ GET BY ID
	@Transactional(readOnly = true)
	public InvoiceDTO getInvoiceById(Long id) {
		return toDTO(findById(id));
	}

	// ✅ GET BY PATIENT
	@Transactional(readOnly = true)
	public List<InvoiceDTO> getInvoicesByPatient(Long patientId) {
		return invoiceRepository.findByPatientId(patientId).stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ CREATE INVOICE
	public InvoiceDTO createInvoice(InvoiceDTO dto) {

		// 1. Validate patient
		Patient patient = patientRepository.findById(dto.getPatientId())
				.orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

		// 2. Map items + calculate line total
		List<InvoiceItem> items = dto.getItems().stream().map(i -> {
			BigDecimal total = i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()));

			return InvoiceItem.builder().description(i.getDescription()).quantity(i.getQuantity())
					.unitPrice(i.getUnitPrice()).totalPrice(total).itemType(i.getItemType()).build();
		}).collect(Collectors.toList());

		// 3. Calculate total invoice amount
		BigDecimal totalAmount = items.stream().map(InvoiceItem::getTotalPrice).reduce(BigDecimal.ZERO,
				BigDecimal::add);

		// 4. Build invoice
		Invoice invoice = Invoice.builder().patient(patient).items(items).totalAmount(totalAmount)
				.paidAmount(BigDecimal.ZERO).paymentStatus(PaymentStatus.PENDING).build();

		// 5. Optional appointment link
		if (dto.getAppointmentId() != null) {
			appointmentRepository.findById(dto.getAppointmentId()).ifPresent(invoice::setAppointment);
		}

		// 6. Set parent reference
		items.forEach(item -> item.setInvoice(invoice));

		// 7. Save
		return toDTO(invoiceRepository.save(invoice));
	}

	// ✅ PROCESS PAYMENT
	public InvoiceDTO processPayment(Long id, BigDecimal amount, Invoice.PaymentMethod method) {

		Invoice invoice = findById(id);

		BigDecimal newPaid = invoice.getPaidAmount().add(amount);

		invoice.setPaidAmount(newPaid);
		invoice.setPaymentMethod(method);

		// Full / Partial payment logic
		if (newPaid.compareTo(invoice.getTotalAmount()) >= 0) {
			invoice.setPaymentStatus(PaymentStatus.PAID);
			invoice.setPaidDate(LocalDateTime.now());
		} else {
			invoice.setPaymentStatus(PaymentStatus.PARTIAL);
		}

		return toDTO(invoiceRepository.save(invoice));
	}

	// ✅ TOTAL REVENUE
	@Transactional(readOnly = true)
	public BigDecimal getTotalRevenue() {
		BigDecimal revenue = invoiceRepository.getTotalRevenue();
		return revenue != null ? revenue : BigDecimal.ZERO;
	}

	// 🔥 HELPER: FIND
	private Invoice findById(Long id) {
		return invoiceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice not found with id: " + id));
	}

	// 🔥 ENTITY → DTO
	private InvoiceDTO toDTO(Invoice inv) {

		List<InvoiceDTO.InvoiceItemDTO> items = inv.getItems() == null ? List.of()
				: inv.getItems().stream()
						.map(i -> InvoiceDTO.InvoiceItemDTO.builder().description(i.getDescription())
								.quantity(i.getQuantity()).unitPrice(i.getUnitPrice()).itemType(i.getItemType())
								.build())
						.collect(Collectors.toList());

		return InvoiceDTO.builder().id(inv.getId()).patientId(inv.getPatient().getId())
				.patientName(inv.getPatient().getFirstName() + " " + inv.getPatient().getLastName())
				.appointmentId(inv.getAppointment() != null ? inv.getAppointment().getId() : null).items(items)
				.totalAmount(inv.getTotalAmount()).paidAmount(inv.getPaidAmount()).paymentStatus(inv.getPaymentStatus())
				.paymentMethod(inv.getPaymentMethod()).build();
	}
}