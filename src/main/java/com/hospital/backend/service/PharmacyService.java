package com.hospital.backend.service;

import com.hospital.backend.dto.MedicineDTO;
import com.hospital.backend.entity.Medicine;
import com.hospital.backend.exception.ResourceNotFoundException;
import com.hospital.backend.repository.MedicineRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PharmacyService {

	// Repository dependency
	private final MedicineRepository medicineRepository;

	// ✅ Get all active medicines
	@Transactional(readOnly = true)
	public List<MedicineDTO> getAllMedicines() {
		return medicineRepository.findByActiveTrue().stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ✅ Get medicine by ID
	@Transactional(readOnly = true)
	public MedicineDTO getMedicineById(Long id) {
		return toDTO(findById(id));
	}

	// ✅ Search medicines by name
	@Transactional(readOnly = true)
	public List<MedicineDTO> searchMedicines(String name) {
		return medicineRepository.findByNameContainingIgnoreCase(name).stream().map(this::toDTO)
				.collect(Collectors.toList());
	}

	// ✅ Get low stock medicines
	@Transactional(readOnly = true)
	public List<MedicineDTO> getLowStockMedicines(int threshold) {
		return medicineRepository.findLowStockMedicines(threshold).stream().map(this::toDTO)
				.collect(Collectors.toList());
	}

	// ✅ Create medicine
	public MedicineDTO createMedicine(MedicineDTO dto) {
		Medicine saved = medicineRepository.save(toEntity(dto));
		return toDTO(saved);
	}

	// ✅ Update medicine
	public MedicineDTO updateMedicine(Long id, MedicineDTO dto) {

		Medicine existing = findById(id);

		existing.setName(dto.getName());
		existing.setManufacturer(dto.getManufacturer());
		existing.setCategory(dto.getCategory());
		existing.setPrice(dto.getPrice());
		existing.setStockQuantity(dto.getStockQuantity());
		existing.setExpiryDate(dto.getExpiryDate());
		existing.setBatchNumber(dto.getBatchNumber());

		return toDTO(medicineRepository.save(existing));
	}

	// ✅ Update stock (increase)
	public MedicineDTO updateStock(Long id, int quantity) {

		Medicine medicine = findById(id);

		medicine.setStockQuantity(medicine.getStockQuantity() + quantity);

		return toDTO(medicineRepository.save(medicine));
	}

	// ✅ Soft delete (active=false)
	public void deleteMedicine(Long id) {

		Medicine medicine = findById(id);
		medicine.setActive(false);

		medicineRepository.save(medicine);
	}

	// ✅ Get expired medicines
	@Transactional(readOnly = true)
	public List<MedicineDTO> getExpiredMedicines() {
		return medicineRepository.findExpiredMedicines().stream().map(this::toDTO).collect(Collectors.toList());
	}

	// ================= HELPER METHODS =================

	private Medicine findById(Long id) {
		return medicineRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Medicine not found with id: " + id));
	}

	private MedicineDTO toDTO(Medicine m) {
		return MedicineDTO.builder().id(m.getId()).name(m.getName()).manufacturer(m.getManufacturer())
				.category(m.getCategory()).price(m.getPrice()).stockQuantity(m.getStockQuantity())
				.expiryDate(m.getExpiryDate()).batchNumber(m.getBatchNumber()).active(m.isActive()).build();
	}

	private Medicine toEntity(MedicineDTO dto) {
		return Medicine.builder().name(dto.getName()).manufacturer(dto.getManufacturer()).category(dto.getCategory())
				.price(dto.getPrice()).stockQuantity(dto.getStockQuantity()).expiryDate(dto.getExpiryDate())
				.batchNumber(dto.getBatchNumber()).active(true).build();
	}
}