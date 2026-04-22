package com.hospital.backend.entity;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;

import java.time.LocalDateTime;

import java.util.List;

/*
=========================================================
ENTITY
=========================================================

Maps Java object

Invoice

to DB table

invoices
=========================================================
*/
@Entity

@Table(name = "invoices")

@Data

@Builder

@NoArgsConstructor

@AllArgsConstructor

public class Invoice {

	/*
	 * ========================================================= PRIMARY KEY
	 * =========================================================
	 */
	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

	/*
	 * ========================================================= Many invoices
	 * 
	 * belong to one patient
	 * =========================================================
	 */
	@ManyToOne(fetch = FetchType.LAZY)

	@JoinColumn(

			name = "patient_id",

			nullable = false

	)

	private Patient patient;

	/*
	 * ========================================================= Optional
	 * relationship:
	 * 
	 * Invoice linked to appointment.
	 * 
	 * Some invoices may be:
	 * 
	 * consultation
	 * 
	 * surgery
	 * 
	 * lab
	 * 
	 * etc. =========================================================
	 */
	@ManyToOne(fetch = FetchType.LAZY)

	@JoinColumn(name = "appointment_id")

	private Appointment appointment;

	/*
	 * ========================================================= ONE INVOICE
	 * 
	 * HAS MANY LINE ITEMS =========================================================
	 * 
	 * This is major concept.
	 * =========================================================
	 */
	@OneToMany(

			/*
			 * mappedBy means:
			 * 
			 * InvoiceItem owns FK.
			 */
			mappedBy = "invoice",

			/*
			 * Cascade ALL
			 * 
			 * save invoice
			 * 
			 * automatically save items
			 */
			cascade = CascadeType.ALL,

			fetch = FetchType.LAZY

	)

	private List<InvoiceItem> items;

	/*
	 * ========================================================= TOTAL BILL AMOUNT
	 * =========================================================
	 * 
	 * Example:
	 * 
	 * 2500.50 =========================================================
	 */
	@Column(

			nullable = false,

			precision = 10,

			scale = 2

	)

	private BigDecimal totalAmount;

	/*
	 * Amount paid by patient.
	 */
	@Column(

			precision = 10,

			scale = 2

	)

	private BigDecimal paidAmount;

	/*
	 * ========================================================= Payment Status
	 * =========================================================
	 */
	@Enumerated(EnumType.STRING)

	private PaymentStatus paymentStatus;

	/*
	 * How patient paid.
	 */
	@Enumerated(EnumType.STRING)

	private PaymentMethod paymentMethod;

	/*
	 * Invoice created date
	 */
	private LocalDateTime invoiceDate;

	/*
	 * Date payment made
	 */
	private LocalDateTime paidDate;

	/*
	 * ========================================================= AUTO EXECUTES
	 * BEFORE INSERT =========================================================
	 */
	@PrePersist

	protected void onCreate() {

		invoiceDate = LocalDateTime.now();

		if (paymentStatus == null)

			paymentStatus = PaymentStatus.PENDING;

	}

	/*
	 * ========================================================= ENUM: PAYMENT
	 * STATUS =========================================================
	 */
	public enum PaymentStatus {

		PENDING,

		PARTIAL,

		PAID,

		CANCELLED

	}

	/*
	 * ========================================================= ENUM: PAYMENT
	 * METHOD =========================================================
	 */
	public enum PaymentMethod {

		CASH,

		CARD,

		UPI,

		INSURANCE,

		ONLINE

	}

}