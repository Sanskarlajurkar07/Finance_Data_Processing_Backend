package com.fintech.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * FinancialRecord entity representing financial transactions with idempotency guarantees.
 * 
 * Stores financial transaction details with BigDecimal precision (NUMERIC 19,4),
 * supports soft delete pattern via isActive flag, and ensures idempotency through
 * unique idempotencyKey field.
 * 
 * Requirements: 4.1, 4.2, 12.3, 13.1
 */
@Entity
@Table(name = "financial_records", indexes = {
    @Index(name = "idx_idempotency_key", columnList = "idempotencyKey", unique = true),
    @Index(name = "idx_record_date", columnList = "recordDate"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_is_active", columnList = "isActive")
})
public class FinancialRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Description is required")
    @Size(min = 1, max = 500, message = "Description must be between 1 and 500 characters")
    @Column(nullable = false, length = 500)
    private String description;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0001", inclusive = true, message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;
    
    @NotNull(message = "Type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RecordType type;
    
    @NotNull(message = "Category is required")
    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    @Column(nullable = false, length = 100)
    private String category;
    
    @NotNull(message = "Record date is required")
    @Column(nullable = false, name = "record_date")
    private LocalDate recordDate;
    
    @Column(unique = true, length = 100, name = "idempotency_key")
    private String idempotencyKey;
    
    @NotNull(message = "Active status is required")
    @Column(nullable = false, name = "is_active")
    private Boolean isActive = true;
    
    @NotNull(message = "Created timestamp is required")
    @Column(nullable = false, updatable = false, name = "created_at")
    private LocalDateTime createdAt;
    
    @NotNull(message = "Updated timestamp is required")
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;
    
    @NotNull(message = "Created by user is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;
    
    /**
     * Default constructor for JPA.
     */
    public FinancialRecord() {
    }
    
    /**
     * Constructor for creating a new financial record.
     * 
     * @param description the transaction description
     * @param amount the transaction amount with NUMERIC(19,4) precision
     * @param type the record type (INCOME or EXPENSE)
     * @param category the transaction category
     * @param recordDate the date of the transaction
     * @param idempotencyKey optional unique key for idempotency guarantee
     * @param createdBy the user who created this record
     */
    public FinancialRecord(String description, BigDecimal amount, RecordType type, 
                          String category, LocalDate recordDate, String idempotencyKey, 
                          User createdBy) {
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.recordDate = recordDate;
        this.idempotencyKey = idempotencyKey;
        this.createdBy = createdBy;
        this.isActive = true;
    }
    
    /**
     * Sets createdAt and updatedAt timestamps before persisting.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    /**
     * Updates the updatedAt timestamp before updating.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    /**
     * Equals based on id for entity identity.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FinancialRecord that = (FinancialRecord) o;
        return Objects.equals(id, that.id);
    }
    
    /**
     * HashCode based on id for entity identity.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    /**
     * String representation for debugging.
     */
    @Override
    public String toString() {
        return "FinancialRecord{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", category='" + category + '\'' +
                ", recordDate=" + recordDate +
                ", idempotencyKey='" + idempotencyKey + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
