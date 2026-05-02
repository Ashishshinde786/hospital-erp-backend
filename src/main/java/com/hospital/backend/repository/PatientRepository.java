package com.hospital.backend.repository;

import com.hospital.backend.entity.Patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    // 🔍 SEARCH
    List<Patient> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName
    );

    // 🩸 BLOOD GROUP
    @Query("SELECT p FROM Patient p WHERE p.bloodGroup=:bloodGroup")
    List<Patient> findByBloodGroup(String bloodGroup);

    // 📊 COUNT
    @Query("SELECT COUNT(p) FROM Patient p")
    long countAllPatients();

    // 🆕 RECENT PATIENTS (FIXED)
    List<Patient> findByCreatedAtAfter(LocalDateTime createdAt);
}