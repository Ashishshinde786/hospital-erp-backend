package com.hospital.backend.repository;

import com.hospital.backend.entity.Medicine;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Repository was missing — added so Spring can detect and wrap this bean.
@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // Returns only active (non-soft-deleted) medicines.
    List<Medicine> findByActiveTrue();

    // Case-insensitive partial-name search (e.g. "para" → Paracetamol).
    List<Medicine> findByNameContainingIgnoreCase(String name);

    // Low-stock alert: stock < threshold AND still active.
    @Query("SELECT m FROM Medicine m WHERE m.stockQuantity < :threshold AND m.active = true")
    List<Medicine> findLowStockMedicines(int threshold);

    // Expired medicines: expiryDate before today AND active.
    @Query("SELECT m FROM Medicine m WHERE m.expiryDate < CURRENT_DATE AND m.active = true")
    List<Medicine> findExpiredMedicines();
}