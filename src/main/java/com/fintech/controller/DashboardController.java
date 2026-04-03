package com.fintech.controller;

import com.fintech.dto.DashboardAnalytics;
import com.fintech.service.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/analytics")
    @PreAuthorize("hasAnyRole('ADMIN', 'ANALYST')")
    public ResponseEntity<DashboardAnalytics> getAnalytics() {
        DashboardAnalytics analytics = dashboardService.getAnalytics();
        return new ResponseEntity<>(analytics, HttpStatus.OK);
    }
}
