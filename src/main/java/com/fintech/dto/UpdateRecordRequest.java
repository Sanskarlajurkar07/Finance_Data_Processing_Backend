package com.fintech.dto;

import com.fintech.model.RecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for updating an existing financial record.
 * Contains all updatable fields for financial record modification.
 * 
 * Validates: Requirements 6.1, 6.3
 */
public class UpdateRecordRequest {
    
    @NotBlank(message = "Description is required")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    private String description;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0001", inclusive = true, message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotNull(message = "Type is required")
    private RecordType type;
    
    @NotBlank(message = "Category is required")
    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    private String category;
    
    @NotNull(message = "Record date is required")
    private LocalDate recordDate;
    
    public UpdateRecordRequest() {
    }
    
    public UpdateRecordRequest(String description, BigDecimal amount, RecordType type, 
                              String category, LocalDate recordDate) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.recordDate = recordDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public RecordType getType() {
        return type;
    }
    
    public void setType(RecordType type) {
        this.type = type;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public LocalDate getRecordDate() {
        return recordDate;
    }
    
    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }
}
