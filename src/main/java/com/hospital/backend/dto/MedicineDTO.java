package com.hospital.backend.dto;

/*
=========================================================
PURPOSE OF THIS CLASS
=========================================================

This DTO represents Medicine data.

Used in:

Pharmacy module

Inventory management

Stock tracking

Medicine CRUD APIs

---------------------------------------------------------

Used for:

Create medicine

Update medicine

Fetch medicine

Search medicine

Track stock

Track expiry

=========================================================
*/

import jakarta.validation.constraints.*;

import lombok.*;

import java.math.BigDecimal;

import java.time.LocalDate;

/*
@Data

Generates:

getters

setters

toString()

equals()

hashCode()
*/
@Data

/*
 * Builder pattern.
 * 
 * Create objects like:
 * 
 * MedicineDTO.builder() .name("Paracetamol") .price(...) .build()
 */
@Builder

/*
 * Empty constructor.
 * 
 * Needed by Jackson.
 */
@NoArgsConstructor

/*
 * Full constructor.
 */
@AllArgsConstructor

public class MedicineDTO {

	/*
	 * ================================================== PRIMARY KEY
	 * ==================================================
	 */
	private Long id;

	/*
	 * ================================================== MEDICINE NAME
	 * ==================================================
	 * 
	 * Required field.
	 * 
	 * Cannot be:
	 * 
	 * null
	 * 
	 * empty
	 * 
	 * blank spaces
	 * 
	 * Examples:
	 * 
	 * Paracetamol
	 * 
	 * Crocin
	 * 
	 * Amoxicillin
	 */
	@NotBlank(message = "Medicine name is required")
	private String name;

	/*
	 * Medicine manufacturer.
	 * 
	 * Examples:
	 * 
	 * Sun Pharma
	 * 
	 * Cipla
	 */
	private String manufacturer;

	/*
	 * Category of medicine.
	 * 
	 * Examples:
	 * 
	 * Antibiotic
	 * 
	 * Painkiller
	 * 
	 * Tablet
	 */
	private String category;

	/*
	 * ================================================== PRICE
	 * ==================================================
	 * 
	 * Cannot be null.
	 * 
	 * Must be >0
	 * 
	 * Cannot be:
	 * 
	 * 0
	 * 
	 * negative ==================================================
	 */
	@NotNull(message = "Price is required")

	@DecimalMin(value = "0.0", inclusive = false)

	private BigDecimal price;

	/*
	 * ================================================== STOCK QUANTITY
	 * ==================================================
	 * 
	 * Inventory count.
	 * 
	 * Examples:
	 * 
	 * 100 tablets
	 * 
	 * 50 bottles
	 * 
	 * Validation:
	 * 
	 * cannot be negative. ==================================================
	 */
	@NotNull(message = "Stock quantity is required")

	@Min(value = 0, message = "Stock cannot be negative")

	private Integer stockQuantity;

	/*
	 * ================================================== EXPIRY DATE
	 * ==================================================
	 * 
	 * Very important in pharmacy.
	 * 
	 * Example:
	 * 
	 * 2027-08-15 ==================================================
	 */
	private LocalDate expiryDate;

	/*
	 * Medicine batch tracking.
	 * 
	 * Example:
	 * 
	 * BATCH-2024-001
	 */
	private String batchNumber;

	/*
	 * ================================================== SOFT DELETE FLAG
	 * ==================================================
	 * 
	 * true = active medicine
	 * 
	 * false = disabled medicine
	 * 
	 * Used instead of physical delete.
	 * ==================================================
	 */
	private boolean active;

}