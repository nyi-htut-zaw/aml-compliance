package com.bank.aml.alert.controller;

import com.bank.aml.alert.dto.AlertDispositionDto;
import com.bank.aml.alert.dto.AlertDto;
import com.bank.aml.alert.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@Tag(name = "Alert Management", description = "Alert queue, assignment, and disposition APIs")
@SecurityRequirement(name = "bearerAuth")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Search and list alerts with filtering and pagination")
    public ResponseEntity<Page<AlertDto>> searchAlerts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) UUID customerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(alertService.searchAlerts(status, severity, customerId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Get alert detail by ID")
    public ResponseEntity<AlertDto> getAlertById(@PathVariable UUID id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @GetMapping("/number/{alertNumber}")
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Get alert by alert number")
    public ResponseEntity<AlertDto> getAlertByNumber(@PathVariable String alertNumber) {
        return ResponseEntity.ok(alertService.getAlertByNumber(alertNumber));
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Assign alert to an analyst user")
    public ResponseEntity<AlertDto> assignAlert(@PathVariable UUID id, @RequestParam UUID userId) {
        return ResponseEntity.ok(alertService.assignAlert(id, userId));
    }

    @PostMapping("/{id}/dispose")
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Dispose alert: close as FP/TP, escalate, or promote to case")
    public ResponseEntity<AlertDto> disposeAlert(
            @PathVariable UUID id,
            @Valid @RequestBody AlertDispositionDto dto) {
        return ResponseEntity.ok(alertService.disposeAlert(id, dto));
    }

    @GetMapping("/metrics")
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Get alert metrics for dashboard KPIs")
    public ResponseEntity<AlertService.AlertMetricsDto> getMetrics() {
        return ResponseEntity.ok(alertService.getAlertMetrics());
    }
}
