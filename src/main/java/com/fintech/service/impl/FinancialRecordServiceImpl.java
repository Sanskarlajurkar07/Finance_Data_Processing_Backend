package com.fintech.service.impl;

import com.fintech.annotation.Auditable;
import com.fintech.dto.CreateRecordRequest;
import com.fintech.dto.RecordFilter;
import com.fintech.dto.UpdateRecordRequest;
import com.fintech.exception.ConflictException;
import com.fintech.exception.RecordNotFoundException;
import com.fintech.model.ActionType;
import com.fintech.model.FinancialRecord;
import com.fintech.model.User;
import com.fintech.repository.FinancialRecordRepository;
import com.fintech.repository.UserRepository;
import com.fintech.service.DashboardService;
import com.fintech.service.FinancialRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of FinancialRecordService.
 * 
 * Manages financial record CRUD operations with idempotency guarantees,
 * soft delete pattern, and BigDecimal precision.
 * 
 * Requirements: 4.1, 4.2, 4.3, 4.4, 5.1, 5.2, 5.3, 5.4, 6.1, 6.2, 6.3, 6.5, 
 *               7.1, 7.2, 7.4, 7.5, 12.2, 12.4, 12.5, 13.1
 */
@Service
@Transactional
public class FinancialRecordServiceImpl implements FinancialRecordService {
    
    private final FinancialRecordRepository financialRecordRepository;
    private final UserRepository userRepository;
    private final DashboardService dashboardService;
    
    public FinancialRecordServiceImpl(FinancialRecordRepository financialRecordRepository,
                                     UserRepository userRepository,
                                     DashboardService dashboardService) {
        this.financialRecordRepository = financialRecordRepository;
        this.userRepository = userRepository;
        this.dashboardService = dashboardService;
    }
    
    /**
     * Creates a financial record with idempotency guarantee.
     * 
     * Validates: Requirements 4.1, 4.2, 4.3, 4.4, 12.2, 12.4, 12.5, 13.1
     */
    @Override
    @Auditable(actionType = ActionType.CREATE)
    public FinancialRecord createRecord(CreateRecordRequest request) {
        // Check for existing idempotency key
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isEmpty()) {
            Optional<FinancialRecord> existingRecord = 
                financialRecordRepository.findByIdempotencyKey(request.getIdempotencyKey());
            
            if (existingRecord.isPresent()) {
                FinancialRecord existing = existingRecord.get();
                
                // Validate idempotency key reuse with different data
                if (!requestMatchesRecord(request, existing)) {
                    throw new ConflictException(
                        "Idempotency key already used with different data: " + 
                        request.getIdempotencyKey()
                    );
                }
                
                // Return existing record
                return existing;
            }
        }
        
        // Get current authenticated user
        User currentUser = getCurrentUser();
        
        // Create new record
        FinancialRecord record = new FinancialRecord();
        record.setDescription(request.getDescription());
        record.setAmount(request.getAmount()); // BigDecimal with NUMERIC(19,4) precision
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setRecordDate(request.getRecordDate());
        record.setIdempotencyKey(request.getIdempotencyKey());
        record.setIsActive(true); // Mark as active
        record.setCreatedBy(currentUser); // Set createdBy to current user
        
        FinancialRecord savedRecord = financialRecordRepository.save(record);
        
        // Invalidate dashboard cache
        dashboardService.invalidateCache();
        
        return savedRecord;
    }
    
    /**
     * Retrieves an active financial record by ID.
     * 
     * Validates: Requirements 5.4
     */
    @Override
    @Transactional(readOnly = true)
    public FinancialRecord getRecordById(Long id) {
        return financialRecordRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new RecordNotFoundException(id));
    }
    
    /**
     * Retrieves active financial records with filtering and pagination.
     * 
     * Validates: Requirements 5.1, 5.2, 5.3
     */
    @Override
    @Transactional(readOnly = true)
    public Page<FinancialRecord> getRecords(RecordFilter filter, Pageable pageable) {
        boolean hasType = filter.getType() != null;
        boolean hasCategory = filter.getCategory() != null && !filter.getCategory().isEmpty();
        boolean hasDateRange = filter.getStartDate() != null && filter.getEndDate() != null;
        
        // Apply filters based on what's provided
        if (hasType && hasCategory && hasDateRange) {
            return financialRecordRepository.findByTypeAndCategoryAndRecordDateBetweenAndIsActiveTrue(
                filter.getType(), filter.getCategory(), filter.getStartDate(), 
                filter.getEndDate(), pageable
            );
        } else if (hasType && hasCategory) {
            return financialRecordRepository.findByTypeAndCategoryAndIsActiveTrue(
                filter.getType(), filter.getCategory(), pageable
            );
        } else if (hasType && hasDateRange) {
            return financialRecordRepository.findByTypeAndRecordDateBetweenAndIsActiveTrue(
                filter.getType(), filter.getStartDate(), filter.getEndDate(), pageable
            );
        } else if (hasCategory && hasDateRange) {
            return financialRecordRepository.findByCategoryAndRecordDateBetweenAndIsActiveTrue(
                filter.getCategory(), filter.getStartDate(), filter.getEndDate(), pageable
            );
        } else if (hasType) {
            return financialRecordRepository.findByTypeAndIsActiveTrue(filter.getType(), pageable);
        } else if (hasCategory) {
            return financialRecordRepository.findByCategoryAndIsActiveTrue(filter.getCategory(), pageable);
        } else if (hasDateRange) {
            return financialRecordRepository.findByRecordDateBetweenAndIsActiveTrue(
                filter.getStartDate(), filter.getEndDate(), pageable
            );
        } else {
            // No filters - return all active records
            return financialRecordRepository.findAllByIsActiveTrue(pageable);
        }
    }
    
    /**
     * Updates a financial record (ADMIN only).
     * 
     * Validates: Requirements 6.1, 6.2, 6.3, 6.5
     */
    @Override
    @Auditable(actionType = ActionType.UPDATE)
    public FinancialRecord updateRecord(Long id, UpdateRecordRequest request) {
        // Retrieve record and verify it's active
        FinancialRecord record = financialRecordRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new RecordNotFoundException(id));
        
        // Update fields while preserving BigDecimal precision
        record.setDescription(request.getDescription());
        record.setAmount(request.getAmount()); // BigDecimal precision preserved
        record.setType(request.getType());
        record.setCategory(request.getCategory());
        record.setRecordDate(request.getRecordDate());
        
        FinancialRecord updatedRecord = financialRecordRepository.save(record);
        
        // Invalidate dashboard cache
        dashboardService.invalidateCache();
        
        return updatedRecord;
    }
    
    /**
     * Soft deletes a financial record (ADMIN only).
     * 
     * Validates: Requirements 7.1, 7.2, 7.4, 7.5
     */
    @Override
    @Auditable(actionType = ActionType.DELETE)
    public void deleteRecord(Long id) {
        // Retrieve record and verify it's active
        FinancialRecord record = financialRecordRepository.findByIdAndIsActiveTrue(id)
            .orElseThrow(() -> new RecordNotFoundException(id));
        
        // Soft delete - set isActive to false without modifying other fields
        record.setIsActive(false);
        
        financialRecordRepository.save(record);
        
        // Invalidate dashboard cache
        dashboardService.invalidateCache();
    }
    
    /**
     * Gets the current authenticated user from SecurityContext.
     * 
     * @return the current authenticated user
     * @throws IllegalStateException if no authenticated user found
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + username));
    }
    
    /**
     * Validates that a request matches an existing record for idempotency check.
     * 
     * @param request the creation request
     * @param record the existing record
     * @return true if request data matches record, false otherwise
     */
    private boolean requestMatchesRecord(CreateRecordRequest request, FinancialRecord record) {
        return Objects.equals(request.getDescription(), record.getDescription()) &&
               Objects.equals(request.getAmount(), record.getAmount()) &&
               Objects.equals(request.getType(), record.getType()) &&
               Objects.equals(request.getCategory(), record.getCategory()) &&
               Objects.equals(request.getRecordDate(), record.getRecordDate());
    }
}
