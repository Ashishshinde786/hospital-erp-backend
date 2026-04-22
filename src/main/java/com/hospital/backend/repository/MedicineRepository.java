package com.hospital.backend.repository;

import com.hospital.backend.entity.Medicine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/*
=========================================================
REPOSITORY: MedicineRepository

Purpose:

Database access layer for Pharmacy module.

Responsible for:

- Save medicines

- Find medicines

- Search medicines

- Detect low stock

- Detect expired medicines

Works with:

Medicine entity

Primary key:

Long

=========================================================
*/

public interface MedicineRepository

		extends JpaRepository<Medicine, Long> {

	/*
	 * ===================================================== Find only ACTIVE
	 * medicines.
	 * 
	 * active=true
	 * 
	 * Why needed?
	 * 
	 * Your delete is soft delete:
	 * 
	 * deleteMedicine()
	 * 
	 * does NOT remove record.
	 * 
	 * It sets:
	 * 
	 * active=false
	 * 
	 * So this query hides inactive medicines.
	 * 
	 * SQL idea:
	 * 
	 * SELECT * FROM medicines WHERE active=true
	 * =====================================================
	 */
	List<Medicine>

			findByActiveTrue();

	/*
	 * ===================================================== SEARCH by partial
	 * medicine name.
	 * 
	 * Containing means:
	 * 
	 * partial match.
	 * 
	 * Example:
	 * 
	 * search:
	 * 
	 * para
	 * 
	 * finds:
	 * 
	 * Paracetamol
	 * 
	 * IgnoreCase means:
	 * 
	 * para
	 * 
	 * PARA
	 * 
	 * Para
	 * 
	 * all work.
	 * 
	 * SQL idea:
	 * 
	 * WHERE lower(name) like '%para%'
	 * =====================================================
	 */
	List<Medicine>

			findByNameContainingIgnoreCase(

					String name

	);

	/*
	 * ===================================================== CUSTOM JPQL QUERY
	 * 
	 * Find medicines where stock is low.
	 * 
	 * Example:
	 * 
	 * threshold=10
	 * 
	 * returns medicines with:
	 * 
	 * stock < 10
	 * 
	 * Also:
	 * 
	 * only active medicines.
	 * 
	 * Used for:
	 * 
	 * inventory alerts
	 * 
	 * reorder alerts
	 * 
	 * dashboard warnings =====================================================
	 */
	@Query(

	"SELECT m FROM Medicine m " +

			"WHERE m.stockQuantity < :threshold " +

			"AND m.active=true"

	)

	List<Medicine>

			findLowStockMedicines(

					int threshold

	);

	/*
	 * ===================================================== Find expired medicines.
	 * 
	 * CURRENT_DATE
	 * 
	 * means database current date.
	 * 
	 * Example:
	 * 
	 * expiryDate < today
	 * 
	 * expired.
	 * 
	 * Used for:
	 * 
	 * compliance
	 * 
	 * safety
	 * 
	 * disposal
	 * 
	 * inventory control =====================================================
	 */
	@Query(

	"SELECT m FROM Medicine m " +

			"WHERE m.expiryDate < CURRENT_DATE " +

			"AND m.active=true"

	)

	List<Medicine>

			findExpiredMedicines();

}