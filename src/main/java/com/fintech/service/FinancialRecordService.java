package com.fintech.service;

import com.fintech.dto.CreateRecordRequest;
import com.fintech.dto.RecordFilter;
import com.fintech.dto.UpdateRecordRequest;
import com.fintech.model.FinancialRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing financial records.
 * 
 * Provides CRUD operations with idempotency guarantees, soft delete pattern,
 * and BigDecimal precision for financial calculations.
 * 
 * Requirements: 4.1, 4.2, 4.3, 5.1, 5.4, 6.1, 7.1, 12.2
 */
public interface FinancialRecordService {
    
    /**
     * Creates a financial record with idempotency guarantee.
     * 
     * If an idempotency key is provided and a record with that key already exists,
     * returns the existing record. If the idempotency key is reused with different
     * data, throws ConflictException.
     * 
     * @param request the record creation request
     * @return the created or existing financial record
     * @throws ConflictException if idempotency key reused with different data
     */
    FinancialRecord createRecord(CreateRecordRequest request);
    
    /**
     * Retrieves an active financial record by ID.
     * 
     * @param id the record ID
     * @return the financial record
     * @throws RecordNotFoundException if record not found or inactive
     */
    FinancialRecord getRecordById(Long id);
    
    /**
     * Retrieves active financial records with filtering and pagination.
     * 
     * @param filter the filter criteria (type, category, date range)
     * @param pageable pagination parameters
     * @return page of active financial records
     */
    Page<FinancialRecord> getRecords(RecordFilter filter, Pageable pageable);
    
    /**
     * Updates a financial record (ADMIN only).
     * 
     * @param id the record ID
     * @param request the update request
     * @return the updated financial record
     * @throws RecordNotFoundException if record not found or inactive
     */
    FinancialRecord updateRecord(Long id, UpdateRecordRequest request);
    
    /**
     * Soft deletes a financial record (ADMIN only).
     * 
     * Marks the record as inactive without physically deleting it.
     * 
     * @param id the record ID
     * @throws RecordNotFoundException if record not found or already inactive
     */
    void deleteRecord(Long id);
}
