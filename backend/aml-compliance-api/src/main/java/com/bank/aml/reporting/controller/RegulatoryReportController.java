package com.bank.aml.reporting.controller;

import com.bank.aml.reporting.dto.ReportCreateDto;
import com.bank.aml.reporting.dto.ReportDto;
import com.bank.aml.reporting.service.RegulatoryReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Regulatory Reporting", description = "SAR/STR/CTR regulatory report lifecycle APIs — DEMO ONLY, not a real submission system")
@SecurityRequirement(name = "bearerAuth")
public class RegulatoryReportController {

    private final RegulatoryReportingService reportingService;

    public RegulatoryReportController(RegulatoryReportingService reportingService) {
        this.reportingService = reportingService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Create a regulatory report (SAR/STR/CTR) from a closed suspicious case — DEMO mode")
    public ResponseEntity<ReportDto> createReport(@Valid @RequestBody ReportCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reportingService.createReport(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'MLRO', 'ADMIN', 'ANALYST', 'SENIOR_ANALYST')")
    @Operation(summary = "Search regulatory reports with filtering")
    public ResponseEntity<Page<ReportDto>> searchReports(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String reportType,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(reportingService.searchReports(status, reportType, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'MLRO', 'ADMIN', 'ANALYST', 'SENIOR_ANALYST')")
    @Operation(summary = "Get regulatory report by ID, including generated goAML JSON")
    public ResponseEntity<ReportDto> getReport(@PathVariable UUID id) {
        return ResponseEntity.ok(reportingService.getReportById(id));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MLRO', 'ADMIN')")
    @Operation(summary = "Approve a report for export (MLRO/Admin only)")
    public ResponseEntity<ReportDto> approveReport(@PathVariable UUID id) {
        return ResponseEntity.ok(reportingService.approveReport(id));
    }

    @PostMapping("/{id}/export")
    @PreAuthorize("hasAnyRole('MLRO', 'ADMIN')")
    @Operation(summary = "Mark an approved report as exported — DEMO mode, no real FIU submission")
    public ResponseEntity<ReportDto> exportReport(@PathVariable UUID id) {
        return ResponseEntity.ok(reportingService.exportReport(id));
    }
}
