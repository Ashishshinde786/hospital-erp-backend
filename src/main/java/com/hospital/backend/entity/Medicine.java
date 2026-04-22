package com.hospital.backend.entity;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;

import java.time.LocalDate;

/*
=========================================================
ENTITY

Maps Java object

Medicine

to DB table

medicines
=========================================================
*/
@Entity

@Table(name = "medicines")

@Data

@Builder

@NoArgsConstructor

@AllArgsConstructor

public class Medicine {

	/*
	 * ========================================================= PRIMARY KEY
	 * =========================================================
	 */
	@Id

	@GeneratedValue(strategy = GenerationType.IDENTITY)

	private Long id;

	/*
	 * ========================================================= MEDICINE NAME
	 * 
	 * Must be unique.
	 * 
	 * No duplicates:
	 * 
	 * Paracetamol Paracetamol
	 * 
	 * not allowed. =========================================================
	 */
	@Column(

			nullable = false,

			unique = true

	)

	private String name;

	/*
	 * Manufacturer
	 * 
	 * Cipla
	 * 
	 * Sun Pharma
	 */
	private String manufacturer;

	/*
	 * Medicine category
	 * 
	 * Antibiotic
	 * 
	 * Painkiller
	 */
	private String category;

	/*
	 * ========================================================= PRICE
	 * =========================================================
	 */
	@Column(

			nullable = false,

			precision = 10,

			scale = 2

	)

	private BigDecimal price;

	/*
	 * ========================================================= CURRENT STOCK
	 * 
	 * Inventory quantity. =========================================================
	 */
	@Column(nullable = false)

	private Integer stockQuantity;

	/*
	 * ========================================================= EXPIRY DATE
	 * 
	 * Critical in pharmacy.
	 * =========================================================
	 */
	private LocalDate expiryDate;

	/*
	 * Batch number.
	 * 
	 * Recall tracking.
	 */
	private String batchNumber;

	/*
	 * ========================================================= SOFT DELETE
	 * 
	 * true = active
	 * 
	 * false = disabled =========================================================
	 */
	@Builder.Default

	private boolean active = true;

}