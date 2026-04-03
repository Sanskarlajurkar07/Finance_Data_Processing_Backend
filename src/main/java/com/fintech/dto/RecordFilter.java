package com.fintech.dto;

import com.fintech.model.RecordType;

import java.time.LocalDate;

/**
 * DTO for filtering financial records.
 * Contains optional filter criteria for querying financial records.
 * 
 * Validates: Requirements 5.2
 */
public class RecordFilter {
    
    private RecordType type;
    private String category;
    private LocalDate startDate;
    private LocalDate endDate;
    
    public RecordFilter() {
    }
    
    public RecordFilter(RecordType type, String category, LocalDate startDate, LocalDate endDate) {
        this.type = type;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
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
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
