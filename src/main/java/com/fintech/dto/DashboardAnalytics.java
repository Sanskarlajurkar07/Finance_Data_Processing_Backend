package com.fintech.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO for dashboard analytics response.
 * Contains aggregated financial metrics with BigDecimal precision.
 * 
 * Validates: Requirements 8.1, 8.2, 8.3
 */
public class DashboardAnalytics {
    
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private Map<String, BigDecimal> categoryBreakdown;
    private LocalDateTime calculatedAt;
    
    public DashboardAnalytics() {
    }
    
    public DashboardAnalytics(BigDecimal totalIncome, BigDecimal totalExpenses, 
                             BigDecimal netBalance, Map<String, BigDecimal> categoryBreakdown,
                             LocalDateTime calculatedAt) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netBalance = netBalance;
        this.categoryBreakdown = categoryBreakdown;
        this.calculatedAt = calculatedAt;
    }
    
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }
    
    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }
    
    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }
    
    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
    
    public BigDecimal getNetBalance() {
        return netBalance;
    }
    
    public void setNetBalance(BigDecimal netBalance) {
        this.netBalance = netBalance;
    }
    
    public Map<String, BigDecimal> getCategoryBreakdown() {
        return categoryBreakdown;
    }
    
    public void setCategoryBreakdown(Map<String, BigDecimal> categoryBreakdown) {
        this.categoryBreakdown = categoryBreakdown;
    }
    
    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
    
    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}
