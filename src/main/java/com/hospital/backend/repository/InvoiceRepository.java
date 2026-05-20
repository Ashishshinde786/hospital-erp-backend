package com.hospital.backend.repository;

import com.hospital.backend.entity.Invoice;
import com.hospital.backend.entity.Invoice.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

// @Repository marks this as a Spring-managed DAO bean.
// Was missing in original — caused potential bean-detection issues.
@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    // Find all invoices for a given patient (billing history).
    List<Invoice> findByPatientId(Long patientId);

    // Filter invoices by payment status (PENDING, PAID, etc.).
    List<Invoice> findByPaymentStatus(PaymentStatus status);

    // Sum of totalAmount for all PAID invoices → used for revenue dashboard.
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.paymentStatus = 'PAID'")
    BigDecimal getTotalRevenue();
}