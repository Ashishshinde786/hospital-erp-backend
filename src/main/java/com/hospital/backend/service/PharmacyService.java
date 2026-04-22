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

/*
=====================================================

PHARMACY SERVICE

Business Logic Layer

Responsible for:

Create medicines

Update medicines

Search medicines

Manage stock

Soft delete medicines

Find low stock medicines

Convert DTO <-> Entity

=====================================================
*/

@Service
/*
 * Marks class as Spring Service Bean.
 * 
 * Spring creates object automatically.
 * 
 * Can be injected into controllers.
 */
@RequiredArgsConstructor
/*
 * Creates constructor for:
 * 
 * private final MedicineRepository medicineRepository
 */
@Transactional
/*
 * All methods run inside transaction.
 * 
 * If something fails:
 * 
 * rollback.
 */
public class PharmacyService {

	/*
	 * Repository layer.
	 * 
	 * Service uses repository to talk to database.
	 */
	private final MedicineRepository medicineRepository;

	/*
	 * ========================================= GET ALL ACTIVE MEDICINES
	 * =========================================
	 * 
	 * Notice:
	 * 
	 * Not using findAll()
	 * 
	 * Using:
	 * 
	 * findByActiveTrue()
	 * 
	 * because soft deleted medicines should not appear.
	 */
	@Transactional(readOnly = true)
	public List<MedicineDTO> getAllMedicines() {

		return medicineRepository

				.findByActiveTrue()

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ========================================= GET MEDICINE BY ID
	 * =========================================
	 */
	@Transactional(readOnly = true)
	public MedicineDTO getMedicineById(Long id) {

		return toDTO(

				findById(id)

		);
	}

	/*
	 * ========================================= SEARCH MEDICINES
	 * 
	 * Example:
	 * 
	 * search "par"
	 * 
	 * could find:
	 * 
	 * Paracetamol
	 */
	@Transactional(readOnly = true)
	public List<MedicineDTO> searchMedicines(

			String name

	) {

		return medicineRepository

				.findByNameContainingIgnoreCase(name)

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ========================================= LOW STOCK CHECK
	 * 
	 * ERP Inventory Feature
	 * 
	 * Example:
	 * 
	 * threshold =10
	 * 
	 * Find medicines where stock < 10
	 */
	@Transactional(readOnly = true)
	public List<MedicineDTO> getLowStockMedicines(

			int threshold

	) {

		return medicineRepository

				.findLowStockMedicines(threshold)

				.stream()

				.map(this::toDTO)

				.collect(Collectors.toList());
	}

	/*
	 * ========================================= CREATE NEW MEDICINE
	 * =========================================
	 */
	public MedicineDTO createMedicine(

			MedicineDTO dto

	) {

		/*
		 * Convert API request -> entity
		 */
		Medicine medicine =

				toEntity(dto);

		/*
		 * Save in DB
		 */
		Medicine saved =

				medicineRepository.save(medicine);

		/*
		 * return response DTO
		 */
		return toDTO(saved);
	}

	/*
	 * ========================================= UPDATE MEDICINE DETAILS
	 * =========================================
	 */
	public MedicineDTO updateMedicine(

			Long id,

			MedicineDTO dto

	) {

		/*
		 * Load existing row
		 */
		Medicine existing =

				findById(id);

		/*
		 * Update fields
		 */
		existing.setName(

				dto.getName());

		existing.setManufacturer(

				dto.getManufacturer());

		existing.setCategory(

				dto.getCategory());

		existing.setPrice(

				dto.getPrice());

		existing.setStockQuantity(

				dto.getStockQuantity());

		existing.setExpiryDate(

				dto.getExpiryDate());

		existing.setBatchNumber(

				dto.getBatchNumber());

		/*
		 * Save updated row
		 */
		return toDTO(

				medicineRepository.save(existing)

		);
	}

	/*
	 * ========================================= UPDATE STOCK
	 * 
	 * VERY IMPORTANT ERP LOGIC =========================================
	 * 
	 * Current stock =100
	 * 
	 * quantity +20
	 * 
	 * New stock =120
	 */
	public MedicineDTO updateStock(

			Long id,

			int quantity

	) {

		Medicine medicine =

				findById(id);

		/*
		 * Current stock
		 */
		int currentStock =

				medicine.getStockQuantity();

		/*
		 * Increase stock
		 */
		medicine.setStockQuantity(

				currentStock + quantity

		);

		return toDTO(

				medicineRepository.save(medicine)

		);
	}

	/*
	 * ========================================= DELETE MEDICINE
	 * 
	 * SOFT DELETE
	 * 
	 * IMPORTANT =========================================
	 * 
	 * NOT:
	 * 
	 * DELETE FROM medicines
	 * 
	 * Instead:
	 * 
	 * active=false
	 */
	public void deleteMedicine(

			Long id

	) {

		Medicine medicine =

				findById(id);

		/*
		 * deactivate medicine
		 */
		medicine.setActive(false);

		/*
		 * save updated status
		 */
		medicineRepository.save(medicine);
	}

	/*
	 * ========================================= HELPER
	 * 
	 * FIND OR THROW EXCEPTION =========================================
	 */
	private Medicine findById(Long id) {

		return medicineRepository.findById(id)

				.orElseThrow(

						() ->

						new ResourceNotFoundException(

								"Medicine not found with id: " + id

						));
	}

	/*
	 * ========================================= ENTITY -> DTO
	 * 
	 * DB -> API RESPONSE =========================================
	 */
	private MedicineDTO toDTO(

			Medicine m

	) {

		return MedicineDTO.builder()

				.id(m.getId())

				.name(m.getName())

				.manufacturer(m.getManufacturer())

				.category(m.getCategory())

				.price(m.getPrice())

				.stockQuantity(m.getStockQuantity())

				.expiryDate(m.getExpiryDate())

				.batchNumber(m.getBatchNumber())

				.active(m.isActive())

				.build();
	}

	/*
	 * ========================================= DTO -> ENTITY
	 * 
	 * API REQUEST -> DB OBJECT =========================================
	 */
	private Medicine toEntity(

			MedicineDTO dto

	) {

		return Medicine.builder()

				.name(dto.getName())

				.manufacturer(dto.getManufacturer())

				.category(dto.getCategory())

				.price(dto.getPrice())

				.stockQuantity(dto.getStockQuantity())

				.expiryDate(dto.getExpiryDate())

				.batchNumber(dto.getBatchNumber())

				/*
				 * new medicines active by default
				 */
				.active(true)

				.build();
	}

}