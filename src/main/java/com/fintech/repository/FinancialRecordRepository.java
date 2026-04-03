package com.fintech.repository;

import com.fintech.model.FinancialRecord;
import com.fintech.model.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for FinancialRecord entity.
 * 
 * Provides CRUD operations and custom query methods for financial record management.
 * Supports filtering by type, category, date range, and pagination.
 * Includes methods for idempotency checking and soft delete filtering.
 * 
 * Requirements: 4.3, 5.1, 5.2, 5.4, 12.2
 */
@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {
    
    /**
     * Finds a financial record by its idempotency key.
     * Used for duplicate detection and idempotency guarantee.
     * 
     * @param idempotencyKey the unique idempotency key
     * @return Optional containing the record if found, empty otherwise
     */
    Optional<FinancialRecord> findByIdempotencyKey(String idempotencyKey);
    
    /**
     * Counts the number of records with the given idempotency key.
     * Used for verifying idempotency guarantees in tests.
     * 
     * @param idempotencyKey the idempotency key to count
     * @return the count of records with this key (should be 0 or 1)
     */
    long countByIdempotencyKey(String idempotencyKey);
    
    /**
     * Finds all active financial records (isActive = true).
     * Excludes soft-deleted records from results.
     * 
     * @return list of all active financial records
     */
    List<FinancialRecord> findAllByIsActiveTrue();
    
    /**
     * Finds an active financial record by ID.
     * Returns the record only if it exists and isActive is true.
     * 
     * @param id the record ID
     * @return Optional containing the record if found and active, empty otherwise
     */
    Optional<FinancialRecord> findByIdAndIsActiveTrue(Long id);
    
    /**
     * Finds all active financial records with pagination support.
     * 
     * @param pageable pagination parameters
     * @return page of active financial records
     */
    Page<FinancialRecord> findAllByIsActiveTrue(Pageable pageable);
    
    /**
     * Finds active financial records filtered by type.
     * 
     * @param type the record type (INCOME or EXPENSE)
     * @param pageable pagination parameters
     * @return page of active records matching the type
     */
    Page<FinancialRecord> findByTypeAndIsActiveTrue(RecordType type, Pageable pageable);
    
    /**
     * Finds active financial records filtered by category.
     * 
     * @param category the category to filter by
     * @param pageable pagination parameters
     * @return page of active records matching the category
     */
    Page<FinancialRecord> findByCategoryAndIsActiveTrue(String category, Pageable pageable);
    
    /**
     * Finds active financial records within a date range.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination parameters
     * @return page of active records within the date range
     */
    Page<FinancialRecord> findByRecordDateBetweenAndIsActiveTrue(
        LocalDate startDate, 
        LocalDate endDate, 
        Pageable pageable
    );
    
    /**
     * Finds active financial records filtered by type and category.
     * 
     * @param type the record type
     * @param category the category
     * @param pageable pagination parameters
     * @return page of active records matching both filters
     */
    Page<FinancialRecord> findByTypeAndCategoryAndIsActiveTrue(
        RecordType type, 
        String category, 
        Pageable pageable
    );
    
    /**
     * Finds active financial records filtered by type and date range.
     * 
     * @param type the record type
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination parameters
     * @return page of active records matching the filters
     */
    Page<FinancialRecord> findByTypeAndRecordDateBetweenAndIsActiveTrue(
        RecordType type,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
    
    /**
     * Finds active financial records filtered by category and date range.
     * 
     * @param category the category
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination parameters
     * @return page of active records matching the filters
     */
    Page<FinancialRecord> findByCategoryAndRecordDateBetweenAndIsActiveTrue(
        String category,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
    
    /**
     * Finds active financial records filtered by type, category, and date range.
     * Supports all filter combinations for comprehensive querying.
     * 
     * @param type the record type
     * @param category the category
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @param pageable pagination parameters
     * @return page of active records matching all filters
     */
    Page<FinancialRecord> findByTypeAndCategoryAndRecordDateBetweenAndIsActiveTrue(
        RecordType type,
        String category,
        LocalDate startDate,
        LocalDate endDate,
        Pageable pageable
    );
    
    /**
     * Custom query to find active records by type with optimized performance.
     * Uses explicit query for better control over SQL generation.
     * 
     * @param type the record type
     * @return list of active records of the specified type
     */
    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.type = :type AND fr.isActive = true")
    List<FinancialRecord> findActiveRecordsByType(@Param("type") RecordType type);
    
    /**
     * Custom query to find active records by category with optimized performance.
     * 
     * @param category the category
     * @return list of active records in the specified category
     */
    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.category = :category AND fr.isActive = true")
    List<FinancialRecord> findActiveRecordsByCategory(@Param("category") String category);
    
    /**
     * Custom query to find active records within a date range.
     * Optimized for dashboard analytics calculations.
     * 
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of active records within the date range
     */
    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.recordDate BETWEEN :startDate AND :endDate AND fr.isActive = true")
    List<FinancialRecord> findActiveRecordsByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
