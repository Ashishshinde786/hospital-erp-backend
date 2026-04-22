package com.hospital.backend.repository;

import com.hospital.backend.entity.Invoice;
import com.hospital.backend.entity.Invoice.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/*
=========================================================
REPOSITORY: InvoiceRepository

Purpose:

Database access layer for Billing/Invoice module.

Handles:

- invoice storage

- invoice retrieval

- payment status search

- revenue calculations

Works with:

Invoice entity

Primary key:

Long

=========================================================
*/

public interface InvoiceRepository

		/*
		 * JpaRepository gives:
		 * 
		 * save()
		 * 
		 * findById()
		 * 
		 * findAll()
		 * 
		 * delete()
		 * 
		 * count()
		 */
		extends JpaRepository<Invoice, Long> {

	/*
	 * ===================================================== Find invoices by
	 * patient.
	 * 
	 * Example:
	 * 
	 * patient id = 1
	 * 
	 * get all bills for patient.
	 * 
	 * Spring derives query automatically.
	 * 
	 * SQL idea:
	 * 
	 * SELECT * FROM invoices WHERE patient_id=?
	 * 
	 * Used for:
	 * 
	 * patient billing history
	 * 
	 * payment tracking
	 * 
	 * billing screen =====================================================
	 */
	List<Invoice>

			findByPatientId(

					Long patientId

	);

	/*
	 * ===================================================== Find invoices by
	 * payment status.
	 * 
	 * Possible status:
	 * 
	 * PENDING
	 * 
	 * PAID
	 * 
	 * PARTIAL
	 * 
	 * CANCELLED
	 * 
	 * Example:
	 * 
	 * show all unpaid invoices.
	 * 
	 * SQL idea:
	 * 
	 * SELECT * FROM invoices WHERE payment_status='PENDING'
	 * =====================================================
	 */
	List<Invoice>

			findByPaymentStatus(

					PaymentStatus status

	);

	/*
	 * ===================================================== CUSTOM JPQL QUERY
	 * 
	 * Calculate total revenue.
	 * 
	 * SUM all invoice amounts
	 * 
	 * ONLY where status = PAID
	 * 
	 * IMPORTANT:
	 * 
	 * only PAID invoices count as revenue.
	 * 
	 * pending invoices are not revenue.
	 * 
	 * JPQL:
	 * 
	 * SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus='PAID'
	 * 
	 * Returns:
	 * 
	 * BigDecimal
	 * 
	 * because money. =====================================================
	 */
	@Query(

	"SELECT SUM(i.totalAmount) " +

			"FROM Invoice i " +

			"WHERE i.paymentStatus='PAID'"

	)

	java.math.BigDecimal

			getTotalRevenue();

}