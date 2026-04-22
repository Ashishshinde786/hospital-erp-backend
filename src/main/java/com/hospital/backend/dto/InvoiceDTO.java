package com.hospital.backend.dto;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This is Invoice DTO.

Represents billing invoice.

Used in:

Hospital billing module.

---------------------------------------------------------

Invoice contains:

Patient

Appointment

Line items

Payment status

Payment method

---------------------------------------------------------

Very important:

This is COMPOSITE DTO

because it contains nested DTO:

InvoiceItemDTO

=========================================================
*/

import com.hospital.backend.entity.Invoice.PaymentMethod;

import com.hospital.backend.entity.Invoice.PaymentStatus;

import com.hospital.backend.entity.InvoiceItem.ItemType;

import jakarta.validation.constraints.*;

import lombok.*;

import java.math.BigDecimal;

import java.util.List;

@Data

@Builder

@NoArgsConstructor

@AllArgsConstructor
public class InvoiceDTO {

	/*
	 * ================================================== INVOICE ID
	 * ==================================================
	 */
	private Long id;

	/*
	 * Patient foreign key.
	 * 
	 * Invoice belongs to patient.
	 */
	@NotNull(message = "Patient ID is required")
	private Long patientId;

	/*
	 * Optional link to appointment.
	 * 
	 * Invoice may be generated
	 * 
	 * from appointment.
	 */
	private Long appointmentId;

	/*
	 * ================================================== LINE ITEMS
	 * ==================================================
	 * 
	 * Very important.
	 * 
	 * Invoice contains multiple items.
	 * 
	 * Examples:
	 * 
	 * Consultation
	 * 
	 * Medicine
	 * 
	 * Lab Test ==================================================
	 */
	@NotEmpty(message = "Invoice must have at least one item")
	private List<InvoiceItemDTO> items;

	/*
	 * Total invoice amount.
	 * 
	 * Example:
	 * 
	 * 1500
	 */
	private BigDecimal totalAmount;

	/*
	 * How much paid.
	 * 
	 * Example:
	 * 
	 * 1000
	 */
	private BigDecimal paidAmount;

	/*
	 * Payment status.
	 * 
	 * PENDING
	 * 
	 * PARTIAL
	 * 
	 * PAID
	 */
	private PaymentStatus paymentStatus;

	/*
	 * Payment method.
	 * 
	 * CASH
	 * 
	 * UPI
	 * 
	 * CARD
	 */
	private PaymentMethod paymentMethod;

	/*
	 * Response enrichment field.
	 * 
	 * Patient full name.
	 */
	private String patientName;

	/*
	 * ========================================================= NESTED DTO
	 * 
	 * Invoice Line Item =========================================================
	 * 
	 * This is child object.
	 * 
	 * Invoice has many items.
	 * 
	 * One invoice
	 * 
	 * Many invoice items.
	 * 
	 * Composition relationship.
	 * =========================================================
	 */
	@Data

	@Builder

	@NoArgsConstructor

	@AllArgsConstructor
	public static class InvoiceItemDTO {

		/*
		 * Item description.
		 * 
		 * Example:
		 * 
		 * Consultation Fee
		 */
		@NotBlank(message = "Description is required")
		private String description;

		/*
		 * Quantity.
		 * 
		 * Must be >=1
		 */
		@NotNull

		@Min(1)

		private Integer quantity;

		/*
		 * Price per unit.
		 * 
		 * Example:
		 * 
		 * 500
		 */
		@NotNull
		private BigDecimal unitPrice;

		/*
		 * Item category.
		 * 
		 * CONSULTATION
		 * 
		 * MEDICINE
		 * 
		 * LAB_TEST
		 */
		private ItemType itemType;

	}

}