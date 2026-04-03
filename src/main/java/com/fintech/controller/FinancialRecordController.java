package com.fintech.controller;

import com.fintech.dto.CreateRecordRequest;
import com.fintech.dto.FinancialRecordResponse;
import com.fintech.dto.RecordFilter;
import com.fintech.dto.UpdateRecordRequest;
import com.fintech.model.FinancialRecord;
import com.fintech.model.RecordType;
import com.fintech.service.FinancialRecordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/financial-records")
public class FinancialRecordController {

    private final FinancialRecordService financialRecordService;

    public FinancialRecordController(FinancialRecordService financialRecordService) {
        this.financialRecordService = financialRecordService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinancialRecordResponse> createRecord(@Valid @RequestBody CreateRecordRequest request) {
        FinancialRecord record = financialRecordService.createRecord(request);
        return new ResponseEntity<>(mapToResponse(record), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<FinancialRecordResponse> getRecord(@PathVariable Long id) {
        FinancialRecord record = financialRecordService.getRecordById(id);
        return new ResponseEntity<>(mapToResponse(record), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST', 'VIEWER')")
    public ResponseEntity<Page<FinancialRecordResponse>> getRecords(
        @RequestParam(required = false) String type,
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String startDate,
        @RequestParam(required = false) String endDate,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        RecordType recordType = null;
        if (type != null && !type.isEmpty()) {
            recordType = RecordType.valueOf(type.toUpperCase());
        }
        
        java.time.LocalDate start = null;
        java.time.LocalDate end = null;
        if (startDate != null && !startDate.isEmpty()) {
            start = java.time.LocalDate.parse(startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            end = java.time.LocalDate.parse(endDate);
        }
        
        RecordFilter filter = new RecordFilter(recordType, category, start, end);
        Pageable pageable = PageRequest.of(page, size);
        Page<FinancialRecord> records = financialRecordService.getRecords(filter, pageable);
        return new ResponseEntity<>(records.map(this::mapToResponse), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinancialRecordResponse> updateRecord(
        @PathVariable Long id,
        @Valid @RequestBody UpdateRecordRequest request
    ) {
        FinancialRecord record = financialRecordService.updateRecord(id, request);
        return new ResponseEntity<>(mapToResponse(record), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        financialRecordService.deleteRecord(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private FinancialRecordResponse mapToResponse(FinancialRecord record) {
        return new FinancialRecordResponse(
            record.getId(),
            record.getDescription(),
            record.getAmount(),
            record.getType(),
            record.getCategory(),
            record.getRecordDate(),
            record.getIdempotencyKey(),
            record.getIsActive(),
            record.getCreatedAt(),
            record.getUpdatedAt(),
            record.getCreatedBy().getId(),
            record.getCreatedBy().getUsername()
        );
    }
}
