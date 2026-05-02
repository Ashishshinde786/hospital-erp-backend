package com.hospital.backend.controller;

import com.hospital.backend.dto.ApiResponse;
import com.hospital.backend.dto.PatientDTO;
import com.hospital.backend.service.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(patientService.getAllPatients(), "Patients fetched successfully")
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(patientService.getPatientById(id), "Patient fetched successfully")
        );
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<PatientDTO>> create(@Valid @RequestBody PatientDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(patientService.createPatient(dto), "Patient created successfully"));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody PatientDTO dto) {

        return ResponseEntity.ok(
                ApiResponse.success(patientService.updatePatient(id, dto), "Patient updated successfully")
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Patient deleted successfully")
        );
    }

    // SEARCH
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> search(@RequestParam String q) {
        return ResponseEntity.ok(
                ApiResponse.success(patientService.searchPatients(q), "Search completed")
        );
    }

    // COUNT
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> count() {
        return ResponseEntity.ok(
                ApiResponse.success(patientService.getTotalCount(), "Count fetched")
        );
    }

    // BLOOD GROUP
    @GetMapping("/blood-group")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getByBloodGroup(@RequestParam String group) {
        return ResponseEntity.ok(
                ApiResponse.success(patientService.getPatientsByBloodGroup(group), "Patients fetched by blood group")
        );
    }

    // AGE RANGE
    @GetMapping("/age-range")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getByAgeRange(
            @RequestParam int min,
            @RequestParam int max) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        patientService.getPatientsByAgeRange(min, max),
                        "Patients fetched by age range"
                )
        );
    }

    // RECENT PATIENTS
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<PatientDTO>>> getRecentPatients(
            @RequestParam(defaultValue = "7") int days) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        patientService.getRecentPatients(days),
                        "Recent patients fetched"
                )
        );
    }
}