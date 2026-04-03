package com.fintech.dto;

import com.fintech.model.RecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new financial record.
 * Contains all required fields for financial record creation with idempotency support.
 * 
 * Validates: Requirements 4.1, 4.4, 12.2
 */
public class CreateRecordRequest {
    
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
    
    @Size(max = 100, message = "Idempotency key must not exceed 100 characters")
    private String idempotencyKey;
    
    public CreateRecordRequest() {
    }
    
    public CreateRecordRequest(String description, BigDecimal amount, RecordType type, 
                              String category, LocalDate recordDate, String idempotencyKey) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.recordDate = recordDate;
        this.idempotencyKey = idempotencyKey;
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
    
    public String getIdempotencyKey() {
        return idempotencyKey;
    }
    
    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
