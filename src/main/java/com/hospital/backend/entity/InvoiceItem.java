package com.hospital.backend.entity;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;

/*
=========================================================
ENTITY

Maps:

InvoiceItem Java object

to

invoice_items table
=========================================================
*/
@Entity

@Table(name = "invoice_items")

@Data

@Builder

@NoArgsConstructor

@AllArgsConstructor

public class InvoiceItem {

	/*
	 * ========================================================= PRIMARY KEY
	 * =========================================================
	 */
	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

	/*
	 * ========================================================= MANY LINE ITEMS
	 * 
	 * BELONG TO ONE INVOICE
	 * =========================================================
	 * 
	 * Child side of relationship.
	 * =========================================================
	 */
	@ManyToOne(fetch = FetchType.LAZY)

	/*
	 * Foreign key:
	 * 
	 * invoice_id
	 * 
	 * Required.
	 */
	@JoinColumn(

			name = "invoice_id",

			nullable = false

	)

	private Invoice invoice;

	/*
	 * ========================================================= Line item
	 * description
	 * 
	 * Examples:
	 * 
	 * Consultation fee
	 * 
	 * Paracetamol
	 * 
	 * Blood Test =========================================================
	 */
	@Column(nullable = false)

	private String description;

	/*
	 * ========================================================= Quantity
	 * 
	 * Examples:
	 * 
	 * 2 medicines
	 * 
	 * 3 tests =========================================================
	 */
	@Column(nullable = false)

	private Integer quantity;

	/*
	 * ========================================================= Price per unit
	 * 
	 * Example:
	 * 
	 * Medicine:
	 * 
	 * 50 each =========================================================
	 */
	@Column(

			nullable = false,

			precision = 10,

			scale = 2

	)

	private BigDecimal unitPrice;

	/*
	 * ========================================================= Computed line total
	 * 
	 * quantity × unitPrice
	 * 
	 * Example:
	 * 
	 * 2 × 50 =100 =========================================================
	 */
	@Column(

			nullable = false,

			precision = 10,

			scale = 2

	)

	private BigDecimal totalPrice;

	/*
	 * ========================================================= Item category
	 * =========================================================
	 */
	@Enumerated(EnumType.STRING)

	private ItemType itemType;

	/*
	 * ========================================================= ENUM
	 * =========================================================
	 */
	public enum ItemType {

		CONSULTATION,

		MEDICINE,

		LAB_TEST,

		PROCEDURE,

		ROOM_CHARGE,

		OTHER

	}

}